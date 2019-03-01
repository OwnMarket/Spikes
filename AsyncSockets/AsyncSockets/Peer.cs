using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Linq;
using System.Threading.Tasks;

namespace SocketsTest
{
    public class Peer
    {
        Socket sender;
        bool connected = false;

        public async Task StartSending()
        {
            // Data buffer for incoming data.
            byte[] bytes = new byte[1024];

            // Connect to a remote device.
            try
            {
                var target = Config.NetworkBootstrapNodes.FirstOrDefault();
                if (target != null)
                {
                    var tokens = target.Split(":");
                    var targetIp = tokens[0];
                    IPHostEntry ipHostInfo = Dns.GetHostEntry(targetIp);
                    IPAddress targetIpAddress = IPAddress.Parse(targetIp);// ipHostInfo.AddressList[0];
                    var targetPort = int.Parse(tokens[1]);
                    IPEndPoint targetEndpoint = new IPEndPoint(targetIpAddress, targetPort);

                    // Create a TCP/IP  socket.
                    sender = new Socket(targetIpAddress.AddressFamily,
                        SocketType.Stream, ProtocolType.Tcp);

                    // Connect the socket to the remote endpoint. Catch any errors.

                    do
                    {
                        try
                        {
                            sender.Connect(targetEndpoint);
                            connected = true;
                            Console.WriteLine("Socket connected to {0}",
                                sender.RemoteEndPoint.ToString());

                            while (true)
                            {
                                // Encode the data string into a byte array.
                                byte[] msg = Encoding.ASCII.GetBytes($"Message from {Config.ListeningAddress}");

                                // Send the data through the socket.
                                int bytesSent = sender.Send(msg);

                                await Task.Delay(3000);
                            }
                        }
                        catch (Exception e)
                        {
                            //
                        }
                    }
                    while (!connected);

                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        public async Task StartListening()
        {
            string data = null;
            // Data buffer for incoming data.
            byte[] bytes = new Byte[1024];

            var tokens = Config.ListeningAddress.Split(":");
            var localIp = tokens[0];
            var localPort = int.Parse(tokens[1]);
            var localIpAddress = IPAddress.Parse(localIp);
            var localEndPoint = new IPEndPoint(localIpAddress, localPort);

            // Create a TCP/IP socket.
            Socket listener = new Socket(localIpAddress.AddressFamily,
                SocketType.Stream, ProtocolType.Tcp);

            // Bind the socket to the local endpoint and
            // listen for incoming connections.
            try
            {
                listener.Bind(localEndPoint);
                listener.Listen(10);
                Console.WriteLine("Waiting for a connection...");
                // Program is suspended while waiting for an incoming connection.

                while (true)
                {
                    Socket handler = listener.Accept();
                    // Start listening for connections.

                    data = null;
                    Console.WriteLine($"{handler.RemoteEndPoint.ToString()} has connected to me");

                    // An incoming connection needs to be processed.
                    while (true)
                    {
                        int bytesRec = handler.Receive(bytes);
                        if (bytesRec <= 1024 && bytesRec > 0)
                        {
                            data += Encoding.ASCII.GetString(bytes, 0, bytesRec);

                            Console.WriteLine("Text received : {0}", data);
                            if (!string.IsNullOrEmpty(Config.PublicAddress))
                            {
                                byte[] msg = Encoding.ASCII.GetBytes($"Reply from {Config.PublicAddress}");
                                //handler.Send(msg);
                                Console.WriteLine("Pretending to send reply");
                            }

                            data = null;
                            await Task.Delay(3000);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                Console.WriteLine(e.ToString());
            }
        }

        public async Task StartWaitingForResponses()
        {
            byte[] bytes = new byte[1024];
            do
            {
                await Task.Delay(1000);
            }
            while (!connected);
            while (true)
            {
                int bytesRec = sender.Receive(bytes);
                Console.WriteLine("{0}",
                    Encoding.ASCII.GetString(bytes, 0, bytesRec));
                await Task.Delay(3000);
            }
        }
    }
}








