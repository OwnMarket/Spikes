// Learn more about F# at http://fsharp.org

open System
open System.Net.Sockets
open System.Threading.Tasks

let connect (id : int) =
    async {

        let port = 13000;
        let client = new TcpClient("127.0.0.1", port)

        let data = System.Text.Encoding.ASCII.GetBytes(id.ToString());
        let stream = client.GetStream()

        do! stream.WriteAsync(data, 0, data.Length) |> Async.AwaitTask
        Console.WriteLine("Sent: {0}", id);

        let! numBytesRead =
            stream.ReadAsync (data, 0, data.Length)
            |> Async.AwaitTask

        let responseData =  System.Text.Encoding.ASCII.GetString(data, 0, numBytesRead);
        Console.WriteLine("Received: {0}", responseData);
        client.Close ()
    }
    |> Async.Start

[<EntryPoint>]
let main argv =
    [1..10000]
    |> List.iter (fun i ->
        Threading.Thread.Sleep 2
        connect i
    )

    Console.ReadLine () |> ignore
    0 // return an integer exit code
