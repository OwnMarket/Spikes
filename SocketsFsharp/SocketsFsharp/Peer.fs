namespace SocketsFsharp

open System
open System.Net
open System.Net.Sockets
open System.Text
open System.Linq

type Peer () =

    let mutable senderSocket : Socket option = None
    member __.StartSending() =
        let target = Config.NetworkBootstrapNodes.FirstOrDefault();
        if target <> null then
            let tokens = target.Split(":");
            let targetIp = tokens.[0];
            let ipHostInfo = Dns.GetHostEntry(targetIp);
            let targetIpAddress = IPAddress.Parse(targetIp);// ipHostInfo.AddressList[0];
            let targetPort = Int32.Parse(tokens.[1]);
            let targetEndpoint = new IPEndPoint(targetIpAddress, targetPort);
            senderSocket <- new Socket(targetIpAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp) |> Some
            senderSocket |> Option.iter(fun sender ->
                let rec tryConnect() =
                    try
                        sender.Connect targetEndpoint
                        Console.WriteLine("Socket connected to {0}", sender.RemoteEndPoint.ToString());
                        let rec loop () =
                            async {
                                let msg = Encoding.ASCII.GetBytes(sprintf "Message from %s" Config.ListeningAddress);
                                sender.Send msg |> ignore
                                let msg2 = Encoding.ASCII.GetBytes (sprintf "DASDAEWAEAEAEAEAEA");
                                sender.Send msg2 |> ignore
                                do! Async.Sleep(3000)
                                return! loop ()
                            }
                        loop ()
                        |> Async.Start
                    with
                    | _ -> tryConnect()
                tryConnect()
            )

    member __.StartListening() =
        let bytes = Array.zeroCreate<byte> 1024;
        let tokens = Config.ListeningAddress.Split(":");
        let localIp = tokens.[0];
        let localPort = Int32.Parse(tokens.[1]);
        let localIpAddress = IPAddress.Parse(localIp);
        let localEndPoint = new IPEndPoint(localIpAddress, localPort);

        Console.WriteLine ("Binding to {0}", Config.ListeningAddress)

        // Create a TCP/IP socket.
        let listener = new Socket(localIpAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp)
        listener.Bind(localEndPoint);
        listener.Listen(10);
        Console.WriteLine("Waiting for a connection...");
        let rec loopAccept () =
            async {
                let handler = listener.Accept();
                // Start listening for connections.

                let mutable data : string = ""
                Console.WriteLine("{0} has connected to me", handler.RemoteEndPoint.ToString());

                let rec loopRead () =
                    async {
                        let bytesRec = handler.Receive(bytes);
                        if (bytesRec <= 1024 && bytesRec > 0) then
                            data <- Encoding.ASCII.GetString(bytes, 0, bytesRec);
                            Console.WriteLine("Text received : {0}", data);

                        if Config.PublicAddress.IsSome then
                            let msg = Encoding.ASCII.GetBytes(sprintf "Reply from %s" Config.PublicAddress.Value);
                            Console.WriteLine("Pretending to send reply");

                        do! Async.Sleep(3000)
                        return! loopRead()
                    }

                loopRead ()
                |> Async.RunSynchronously
                return! loopAccept()
            }
        loopAccept()
        |> Async.RunSynchronously



