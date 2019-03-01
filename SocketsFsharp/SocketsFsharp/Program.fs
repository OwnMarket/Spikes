// Learn more about F# at http://fsharp.org

open System
open SocketsFsharp

[<EntryPoint>]
let main argv =
    printfn "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
    let peer = new Peer();
    async {
        peer.StartSending()
    }
    |> Async.Start
    Console.WriteLine ("Started sending")
    async {
        peer.StartListening()
    }
    |> Async.Start
    Console.WriteLine ("Started listening")
    Console.ReadLine() |> ignore

    0 // return an integer exit code
