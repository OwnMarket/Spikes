namespace Chainium.Network.Gossip

module Cli = 
    open Membership

    let handleStartNode configFile =       
        let config = Config.Get configFile
        match config with 
        | Some conf -> 
            let gossipNode = GossipNode(conf)
            gossipNode.Start()  
        | None -> Log.error "Invalid config file"

    let handleCommand args =        
        match args with
        | ["-config"; configFile] -> handleStartNode configFile |> ignore
        | _ -> Log.info "Usage: -config fileName"
