// Learn more about F# at http://fsharp.org

open System.Threading
open System.Globalization
open Chainium.Network.Gossip

[<EntryPoint>]
let main argv =
    Log.info "Starting .."

    try
        Thread.CurrentThread.CurrentCulture <- CultureInfo.InvariantCulture
        Thread.CurrentThread.CurrentUICulture <- CultureInfo.InvariantCulture

        argv |> Array.toList |> Cli.handleCommand
    with
    | ex -> Log.error ex.StackTrace
    0 // return an integer exit code
