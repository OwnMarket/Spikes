// Learn more about F# at http://fsharp.org

open System
open System.Net.Sockets
open System.Net
open System.Threading

let tcpClientConnected = new ManualResetEvent(false);
let server = new TcpListener (IPAddress.Any, 13000)

let acceptClientConnected (ar : IAsyncResult) =
    // Get the listener that handles the client request.
    let listener = ar.AsyncState :?> TcpListener

    // End the operation and create the tcp client
    // to handle communication with the remote host.
    let client = listener.EndAcceptTcpClient ar

    // Process the connection here.
    Console.WriteLine "Client connected completed"

    // Signal the calling thread to continue.
    tcpClientConnected.Set() |> ignore

    let bytes = Array.zeroCreate<byte>(256)
    let stream = client.GetStream()

    let mutable readBytes = 0;
    let mutable msg = Array.zeroCreate<byte>(200)
    let mutable data = ""

    let rec readOnly () =
        async {
            // Console.WriteLine "Reading reading"

            let! bts =
                stream.ReadAsync(bytes, 0, bytes.Length)
                |> Async.AwaitTask

            Console.WriteLine (sprintf "Read %i" bts)
            readBytes <- bts
            if readBytes <> 0 then

                data <- System.Text.Encoding.ASCII.GetString(bytes, 0, readBytes);
                Console.WriteLine("Received: {0}", data);
                return! readOnly()
        }

    let rec writeOnly () =
        async {
            let mutable dataSent = false;
            let rec reply () =
                async {
                    if not (String.IsNullOrEmpty(data)) then
                        let data = data.ToUpper()
                        msg <- System.Text.Encoding.ASCII.GetBytes(data)

                        // Send back a response.
                        do! stream.AsyncWrite(msg, 0, msg.Length)

                        Console.WriteLine "Server sent data"
                        dataSent <- true
                        client.Close()
                    else
                        if not dataSent then
                            return! reply()
                }
            reply () |> Async.Start
        }

    readOnly () |> Async.Start
    writeOnly () |> Async.Start

    // stream.Close()
    // client.Close()


let startReading () =
    server.Start ()

    while true do
        tcpClientConnected.Reset() |> ignore
        Console.WriteLine("Waiting for a connection...");

        // Accept the connection.
        // BeginAcceptSocket() creates the accepted socket.
        server.BeginAcceptTcpClient(new AsyncCallback(acceptClientConnected), server) |> ignore

        // Wait until a connection is made and processed before
        // continuing.
        tcpClientConnected.WaitOne() |> ignore



[<EntryPoint>]
let main argv =

    async {
        startReading ()
    }
    |> Async.Start

    Console.ReadLine () |> ignore

    0
