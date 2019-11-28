// Learn more about F# at http://fsharp.org

open System
open System.Net.Sockets
open System.Threading.Tasks

let connect (id) =
    async {
        let port = 13000;
        let client = new TcpClient("127.0.0.1", port)

        let data = System.Text.Encoding.ASCII.GetBytes(sprintf "This is soething very important with the %i" id);
        let stream = client.GetStream()

        do! stream.WriteAsync(data, 0, data.Length) |> Async.AwaitTask

        Console.WriteLine("Sent: {0}", id);

        let rec readOnly (sm : NetworkStream) =
            let mutable numBytesRead = 0
            async {

                let! bts =
                    sm.ReadAsync (data, 0, data.Length)
                    |> Async.AwaitTask


                numBytesRead <- bts
                if numBytesRead <> 0 then

                    let responseData =  System.Text.Encoding.ASCII.GetString(data, 0, numBytesRead);
                    Console.WriteLine("Received: {0}", responseData);
                    return! readOnly sm
            }

        readOnly (stream) |> Async.Start

    }
    |> Async.Start


[<EntryPoint>]
let main argv =
    async {
        [1..10000]
        |> List.iter (fun i ->
            let index = i
            async {
                connect index
            }
            |> Async.Start
        )
    } |> Async.Start



    Console.ReadLine () |> ignore
    0 // return an integer exit code
