namespace SocketsFsharp

open System.IO
open Newtonsoft.Json
open Microsoft.Extensions.Configuration
open System.Reflection

type Config () =
    static let appDir = Directory.GetCurrentDirectory();

    static let config =
        ConfigurationBuilder()
            .SetBasePath(appDir)
            .AddJsonFile("Config.json")
            .Build()

    static member ListeningAddress
        with get () =
            let listeningAddress = config.["ListeningAddress"]
            if listeningAddress.IsNullOrWhiteSpace() then
                "*:25718"
            else
                listeningAddress

    static member PublicAddress
        with get () =
            let publicAddress = config.["PublicAddress"]
            if publicAddress.IsNullOrWhiteSpace() then
                None
            else
                Some publicAddress

    static member NetworkBootstrapNodes
        with get () =
            config.GetSection("NetworkBootstrapNodes").GetChildren()
            |> Seq.map (fun c -> c.Value)
            |> Seq.toList