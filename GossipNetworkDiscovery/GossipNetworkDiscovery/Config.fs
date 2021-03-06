﻿namespace Chainium.Network.Gossip

open System.IO
open Newtonsoft.Json

type Config () =        
    static member Get configFile = 
        try 
            configFile 
            |> File.ReadAllText 
            |> JsonConvert.DeserializeObject<NodeConfig> 
            |> Some
        with
        | _ -> None
