namespace Chainium.Network.Gossip

open System.Collections.Concurrent
open Chainium.Common
open NetMQ
open NetMQ.Sockets
open Newtonsoft.Json

module Transport =

    let generateId address port = 
        sprintf "%s:%i" address port 

    let packMessage message = 
        let json = message |> JsonConvert.SerializeObject
        EncryptionHelper.encrypt json "password" "salt"  

    let unpackMessage message = 
        let json =  EncryptionHelper.decrypt message "password" "salt"
        json |> JsonConvert.DeserializeObject<PeerMessage>       

    let send (msg : string) address port = 
        let host = generateId address port
        let clientSocket = new RequestSocket(">tcp://" + host)                    
        clientSocket.TrySendFrame msg |> ignore
        clientSocket.SendReady |> Observable.subscribe (fun _ -> (clientSocket.Dispose())) |> ignore

    let sendGossipDiscoveryMessage gossipDiscoveryMessage =            
        let msg = packMessage (GossipDiscoveryMessage gossipDiscoveryMessage)
        send msg gossipDiscoveryMessage.IPAddress gossipDiscoveryMessage.Port    
        
    let sendMulticastMessage (connections: ConcurrentDictionary<'T, Member>) sourceId multicastMessage =
        let multicastGroupMembers = 
            connections
            |> Map.ofDict 
            |> Map.filter (fun key _ -> sourceId <> key)  
            |> Seq.toList
           
        match multicastGroupMembers with 
        | [] -> ()           
        | _ ->                 
            multicastGroupMembers
            |> Seq.shuffleG                
            |> Helpers.seqKeyValuePairToList                
            |> List.iter (fun m ->                    
                let msg = packMessage (MulticastMessage multicastMessage) 
                send msg m.IPAddress m.Port)            

    let sendGossipMessage gossipMessage =
        //TODO
        ()

    let sendMessage connections sourceId message =
        match message with 
        | GossipDiscoveryMessage m -> sendGossipDiscoveryMessage m             
        | MulticastMessage m  -> sendMulticastMessage connections sourceId m
        | GossipMessage m -> sendGossipMessage m
    
    let receiveGossipDiscoveryMessage callback message = 
        callback message

    let receiveMulticastMessage message =         
        printfn "Received multicast message from %s " (message.ToString())

    let receiveGossipMessage message =         
        printfn "Received gossip message from %s " (message.ToString())

    let receiveMessage host discoveryCallback = 
        let serverSocket = new ResponseSocket("@tcp://" + host)        
        let poller = new NetMQPoller()  
        poller.Add serverSocket

        serverSocket.ReceiveReady 
        |> Observable.subscribe (fun eventArgs -> 
            let received, message = eventArgs.Socket.TryReceiveFrameString()                
            if received then 
                eventArgs.Socket.SendFrame "Ack"           
                let peerMessage = unpackMessage message
                match peerMessage with 
                | GossipDiscoveryMessage m -> receiveGossipDiscoveryMessage discoveryCallback m
                | MulticastMessage m -> receiveMulticastMessage m 
                | GossipMessage m -> receiveGossipMessage m
        )
        |> ignore

        poller.RunAsync()