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
        let gossipMessages = new ConcurrentDictionary<Guid, GossipMessage * string list>()        

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
         
        let updateGossipMessagesProcessingQueue ids gossipMessage = 
            let found, result = gossipMessages.TryGetValue gossipMessage.MessageId
            if found then                    
                gossipMessages.AddOrUpdate(
                    gossipMessage.MessageId,
                    (gossipMessage, ids @ (snd result)),
                    fun _ _ -> (gossipMessage, ids @ (snd result))) |> ignore
            else
                gossipMessages.AddOrUpdate(
                    gossipMessage.MessageId,
                    (gossipMessage, ids),
                    fun _ _ -> (gossipMessage, ids)) |> ignore    
                        
        member __.Id  
            with get () = id   
            
        member __.Start () =
            __.StartNode()
            __.StartGossip()
       
        member __.SendMessage message = 
            match message with
            | GossipDiscoveryMessage _ | MulticastMessage _ -> 
                Transport.sendMessage (__.GetActiveMembers()) __.Id message 
            | GossipMessage m -> __.SendGossipMessage m                                

        member __.StartNode () =
            Log.info "Start node .."
            __.AddMember None
            __.StartServer()       
                
        member  __.StartGossip () = 
            Log.info "Start Gossip"
            let rec loop () = 
                async {                    
                    __.SendMembership None       
                    do! Async.Sleep(config.Cycle)

                    return! loop ()
                }
            Async.RunSynchronously (loop ()) 

        member private __.SendGossipMessageToRecipient recipientId gossipMessage = 
            let found, recipientMember = activeMembers.TryGetValue recipientId
            if found then 
                Log.infof "Sending gossip message %A to %s" gossipMessage.MessageId recipientId
                Transport.sendMessage [recipientMember] __.Id (GossipMessage gossipMessage)  
                
        member private __.ProcessGossipMessage gossipMessage recipientIds = 
            match recipientIds with 
            (*
                No recipients left to send message to, remove gossip message from the processing queue
            *)
            | [] -> gossipMessages.TryRemove gossipMessage.MessageId |> ignore
            (*
                One recipient left to send the message to:
                If gossip message was processed before, append it to the processed recipients list for that message
                If not, add the gossip message (and the corresponding recipient) to the processing queue
            *)
            | [recipientId] ->                 
                __.SendGossipMessageToRecipient recipientId gossipMessage
                updateGossipMessagesProcessingQueue [recipientId] gossipMessage
            (*
                If two or more recipients left, select randomly a subset (fanout) of recipients to send the gossip message to
                If gossip message was processed before, append the selected recipients to the processed recipients list for that message
                If not, add the gossip message (and the corresponding recipient) to the processing queue
            *)
            | _ -> 
                let selectedRecipientIds = 
                    recipientIds  
                    |> Seq.shuffleG
                    |> Seq.toList
                    |> List.take fanout

                selectedRecipientIds |> List.iter (fun recipientId -> __.SendGossipMessageToRecipient recipientId gossipMessage)
                updateGossipMessagesProcessingQueue selectedRecipientIds gossipMessage                                                      

        member private __.SendGossipMessage message =  
            let rec loop msg = 
                async {
                    let recipientIds = 
                        __.GetActiveMembers() 
                        |> List.map (fun m -> m.Id) 
                        |> List.filter (fun i -> i <> __.Id)

                    // New Message
                    if msg.MessageId = Guid.Empty then
                        let gossipMessage = {
                            MessageId = Guid.NewGuid()
                            SenderId = __.Id
                            ProcessedIds = [__.Id]
                            Data = msg.Data
                        }
                    
                        __.ProcessGossipMessage gossipMessage recipientIds

                        if recipientIds.Length > 1 then 
                            do! Async.Sleep(config.Cycle)
                            return! loop gossipMessage
                    // Existing message
                    else 
                        match gossipMessages.TryGetValue msg.MessageId with
                        (* 
                            Gossip message partially processed by the node, 
                            Remove sender and the processed recipients from the new selection of recipients 
                        *)
                        | true, result ->                     
                            let newRecipientIds = List.except recipientIds ((snd result) @ [msg.SenderId] @ msg.ProcessedIds)
                            __.ProcessGossipMessage msg newRecipientIds

                            if newRecipientIds.Length > 1 then 
                                do! Async.Sleep(config.Cycle)
                                return! loop msg
                        (* 
                            Gossip message, not processed by this node
                            Remove sender from the new selection of recipients
                        *)
                        | false, _ -> 
                            let newRecipientIds = List.except recipientIds ([msg.SenderId] @ msg.ProcessedIds)
                            __.ProcessGossipMessage msg newRecipientIds

                            if recipientIds.Length > 1 then 
                                do! Async.Sleep(config.Cycle)
                                return! loop msg
                }
            
            Async.Start (loop message)        
        
        member private __.ReceiveGossipMessage gossipMessage = 
            let processed, _  = gossipMessages.TryGetValue gossipMessage.MessageId 
            if not processed then
                Log.infof "*** RECEIVED GOSSIP MESSAGE %A from %s " gossipMessage.MessageId gossipMessage.SenderId
                
                // Make sure the message is not processed twice
                gossipMessages.AddOrUpdate(
                    gossipMessage.MessageId,
                    (gossipMessage, []),
                    fun _ _ -> (gossipMessage, [])) |> ignore 

                // Add self to the list of processed Ids
                let msg = GossipMessage {
                    MessageId = gossipMessage.MessageId
                    SenderId = __.Id
                    ProcessedIds = [__.Id] @ gossipMessage.ProcessedIds |> List.distinct
                    Data = gossipMessage.Data
                }

                // Once a node is infected, propagate the message further
                __.SendMessage msg
            else 
                Log.infof "Received gossip message %A from %s -> Already processed!" gossipMessage.MessageId gossipMessage.SenderId

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
            Transport.receiveMessage __.Id __.ReceiveMembers __.ReceiveGossipMessage           
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
                |> Helpers.seqOfKeyValuePairToList
                |> Some

        member private __.IncreaseHeartbeat () =             
            match __.GetActiveMember __.Id with
            | Some m -> 
                let localMember = {
                    Id = m.Id
                    IPAddress = m.IPAddress
                    Port = m.Port
                    Heartbeat = m.Heartbeat + 1
                }                
                activeMembers.AddOrUpdate (__.Id, localMember, fun _ _ -> localMember) |> ignore   
            | None -> ()
                                                  
        member private __.ReceiveActiveMember inputMember =         
            match __.GetActiveMember inputMember.Id with
            | Some m ->  
                let localMember = {
                    Id = m.Id
                    IPAddress = m.IPAddress
                    Port = m.Port
                    Heartbeat = inputMember.Heartbeat
                }                                
                activeMembers.AddOrUpdate (inputMember.Id, localMember, fun _ _ -> localMember) |> ignore
                restartTimer inputMember.Id |> ignore
            | None -> ()
            
        member private __.GetActiveMembers () =
            activeMembers |> Helpers.seqOfKeyValuePairToList 
        
        member private __.GetDeadMembers () =
            deadMembers |> Helpers.seqOfKeyValuePairToList 
        
