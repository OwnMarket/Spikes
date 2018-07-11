namespace Chainium.Network.Gossip

open System
open System.Threading
open System.Collections.Concurrent
open Chainium.Common

module Membership =     

    type GossipNode(config) =             

        let activeMembers = new ConcurrentDictionary<string, Member>()
        let deadMembers = new ConcurrentDictionary<string, Member>()  
        let timers = new ConcurrentDictionary<string, System.Timers.Timer>()

        let fanout = 2    
            
        let getInstanceId =
            let localNodes = config.InitialNodes |> List.filter (fun n -> n.Local = true)
            match localNodes with
                | [localNode] -> Transport.generateId localNode.IPAddress localNode.Port
                | _ ->  ""

        let id = getInstanceId                   

        let printActiveMembers () =             
            printfn "\n========= ACTIVE CONNECTIONS [%s] =========" (DateTimeOffset.Now.ToString("yyyy-MM-dd HH:mm:ss.fff"))           
            activeMembers 
            |> Map.ofDict
            |> Seq.toList
            |> List.iter (fun x -> printfn "%s Heartbeat:%i" x.Key x.Value.Heartbeat) 
            printfn "================================================================\n"

        (* 
            A member is dead if it's in the list of dead-members and 
            the heartbeat the local node is bigger than the one passed by argument.
        *)
        let isDead inputMember =       
            let foundDead, deadMember = deadMembers.TryGetValue inputMember.Id
            if foundDead then
                Log.infof "Received a node with heartbeat %i, in dead-members it has heartbeat %i" inputMember.Heartbeat deadMember.Heartbeat
                (deadMember.Heartbeat >= inputMember.Heartbeat)                
            else 
                false
        (*
            Once a member has been declared dead and it hasn't recovered in
            2xTFail time is removed from the dead-members list.
            So if node has been down for a while and come back it can be added again.
            Here this will be scheduled right after a node is declared, so total time
            elapsed is 2xTFail
         *)
        let setFinalDeadMember id =         
            let found, _ = activeMembers.TryGetValue id
            if not found then
                Log.errorf "*** Member marked as DEAD %s" id                
                deadMembers.TryRemove id |> ignore
                timers.TryRemove id |> ignore

        (*
            It declares a member as dead. 
            - remove it from active nodes
            - add it to dead nodes 
            - remove its timers
            - set to be removed from the dead-nodes. so that if it recovers can be added 
        *)
        let setPendingDeadMember id =                      
            Log.warningf "*** Member potentially DEAD: %s" id
            let found, activeMember = activeMembers.TryGetValue id
            if found then                 
                activeMembers.TryRemove id |> ignore
                deadMembers.AddOrUpdate (id, activeMember, fun _ _ -> activeMember) |> ignore
                timers.TryRemove id |> ignore  
                let timer = Timer.createTimer config.TFail (fun _ -> (setFinalDeadMember id))
                timer.Start()
                timers.AddOrUpdate (id, timer, fun _ _ -> timer) |> ignore                          
            
        let restartTimer id =   
            Timer.restartTimer timers id config.TFail (fun _ -> (setPendingDeadMember id))                       
            
        member __.Id  
            with get () = id   
            
        member __.Start () =
            __.StartNode()
            __.StartGossip()
       
        member __.SendMessage message = 
            Transport.sendMessage activeMembers __.Id message            

        member __.GetActiveMembers () =
            activeMembers |> Helpers.seqKeyValuePairToList 
        
        member __.GetDeadMembers () =
            deadMembers |> Helpers.seqKeyValuePairToList 

        member private __.StartNode () =
            Log.info "Start node .."
            __.AddMember None
            __.StartServer()       

        member private __.StartGossip () = 
            Log.info "Start Gossip"
            let rec loop () = 
                async {                    
                    __.SendMembership None       
                    do! Async.Sleep(config.Cycle)

                    return! loop ()
                }
            Async.RunSynchronously (loop ()) 
            
        member private __.AddMember inputMember =
            let rec loop (mem : Member option ) = 
                match mem with 
                | Some m -> 
                    Log.infof "Adding new member : %s port %i" m.IPAddress m.Port            
                    let id = Transport.generateId m.IPAddress m.Port
                    activeMembers.AddOrUpdate (id, m, fun _ _ -> m) |> ignore

                    let isCurrentNode = m.Id = __.Id
                    if not isCurrentNode then 
                        restartTimer m.Id |> ignore
                | None ->
                    config.InitialNodes 
                    |> List.map (fun n -> 
                       {
                           Id = Transport.generateId n.IPAddress n.Port
                           IPAddress = n.IPAddress
                           Port = n.Port
                           Heartbeat = 0
                       }) 
                    |> List.iter (fun m -> loop (Some m))

            loop inputMember    

        member private __.IsCurrentNode key = 
            key = __.Id             
        
        member private __.GetActiveMember id =
            let found, localMember = activeMembers.TryGetValue id
            if found then 
                Some localMember
            else 
                None         
                
        member private __.MergeMember inputMember = 
            if not (__.IsCurrentNode inputMember.Id) then           
                Log.infof "Receive member: %s ..." inputMember.Id                                
                match __.GetActiveMember inputMember.Id with      
                | Some localMember ->
                    if localMember.Heartbeat < inputMember.Heartbeat then 
                        __.ReceiveActiveMember inputMember                   
                    
                | None -> 
                    if not (isDead inputMember) then
                        __.AddMember (Some inputMember)
                        deadMembers.TryRemove inputMember.Id |> ignore
                                    
        member private __.MergeMemberList members = 
            members |> List.iter (fun m -> __.MergeMember m)   
            
        member private __.ReceiveMembers msg = 
            __.MergeMemberList msg.ActiveMembers

        member private __.StartServer () =
            Log.infof "Starting server in %s" __.Id 
            Transport.receiveMessage __.Id __.ReceiveMembers            
            Log.info "Server started .."
            
        member private __.SendMembership inputMember = 
            let rec loop mem =                
                match mem with 
                | Some m ->                     
                    Log.infof "Sending memberlist to: %s" m.Id                  
                    let gossipDiscoveryMessage = GossipDiscoveryMessage {
                        IPAddress = m.IPAddress
                        Port = m.Port
                        ActiveMembers = __.GetActiveMembers()
                    }                        
                    __.SendMessage gossipDiscoveryMessage

                | None ->                     
                    __.IncreaseHeartbeat()
                    match __.SelectRandomMembers() with 
                        | None -> () 
                        | Some members -> members |> List.iter (fun m -> loop (Some m))

                    printActiveMembers ()
            loop inputMember

        // Returns N (fanout) members from the memberlist without current Id
        member private __.SelectRandomMembers () = 
            let connectedMembers = 
                activeMembers
                |> Map.ofDict 
                |> Map.filter (fun key _ -> not (__.IsCurrentNode key) )  
                |> Seq.toList
           
            match connectedMembers with 
            | [] -> None
            | [mem] -> Some [mem.Value]
            | _ ->                 
                connectedMembers
                |> Seq.shuffleG
                |> Seq.take fanout
                |> Helpers.seqKeyValuePairToList
                |> Some

        member private __.IncreaseHeartbeat () =             
            match __.GetActiveMember __.Id with
            | Some localMember -> 
                localMember.Heartbeat <- Interlocked.Increment (ref localMember.Heartbeat)
                activeMembers.AddOrUpdate (__.Id, localMember, fun _ _ -> localMember) |> ignore   
            | None -> ()
                                                  
        member private __.ReceiveActiveMember inputMember =         
            match __.GetActiveMember inputMember.Id with
            | Some localMember ->               
                localMember.Heartbeat <- Interlocked.Exchange ((ref inputMember.Heartbeat), inputMember.Heartbeat)  
                activeMembers.AddOrUpdate (inputMember.Id, localMember, fun _ _ -> localMember) |> ignore
                restartTimer inputMember.Id |> ignore
            | None -> ()
            
        
