namespace Chainium.Network.Gossip

open Membership

module Cli = 
    let handleStartGossipDiscovery configFile =       
        let config = Config.Get configFile
        match config with 
        | Some conf -> 
            let gossipNode = GossipNode(conf)
            gossipNode.Start()  
        | None -> Log.error "Invalid config file"

    let handleSendMulticast configFile multicastMessage = 
        let config = Config.Get configFile
        match config with 
        | Some conf -> 
            let gossipNode = GossipNode(conf)
            gossipNode.StartNode()      
            gossipNode.SendMessage (MulticastMessage multicastMessage)
        | None -> Log.error "Invalid config file"

    let handleCommand args =        
        match args with
        | ["-config"; configFile] -> handleStartGossipDiscovery configFile |> ignore
        | ["-config"; configFile; "-multicast"; multicastMessage] -> handleSendMulticast configFile multicastMessage |> ignore
        | _ -> Log.info "Usage: -config fileName"
