using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Threading.Tasks;

namespace TcpDiscovery
{
    class TCPServer
    {
        // Thread signal.
        public static ManualResetEvent tcpClientConnected =
            new ManualResetEvent(false);

        public static void StartReading()
        {
            TcpListener server = null;
            try
            {
                // Set the TcpListener on port 13000.
                Int32 port = 13000;

                // TcpListener server = new TcpListener(port);
                server = new TcpListener(IPAddress.Any, port);

                // Start listening for client requests.
                server.Start();

                // Enter the listening loop.
                while (true)
                {
                    tcpClientConnected.Reset();

                    // Start to listen for connections from a client.
                    Console.WriteLine("Waiting for a connection...");

                    // Accept the connection.
                    // BeginAcceptSocket() creates the accepted socket.
                    server.BeginAcceptTcpClient(
                        new AsyncCallback(DoAcceptTcpClientCallback),
                        server);

                    // Wait until a connection is made and processed before
                    // continuing.
                    tcpClientConnected.WaitOne();
                }
            }
            catch (SocketException e)
            {
                Console.WriteLine("SocketException: {0}", e);
            }
            finally
            {
                // Stop listening for new clients.
                server.Stop();
            }

            Console.WriteLine("\nHit enter to continue...");
            Console.Read();
        }

        public static void DoAcceptTcpClientCallback(IAsyncResult ar)
        {
            // Get the listener that handles the client request.
            TcpListener listener = (TcpListener)ar.AsyncState;

            // End the operation and display the received data on
            // the console.
            TcpClient client = listener.EndAcceptTcpClient(ar);

            // Process the connection here. (Add the client to a
            // server table, read data, etc.)
            Console.WriteLine("Client connected completed");

            // Signal the calling thread to continue.
            tcpClientConnected.Set();

            // Buffer for reading data
            var bytes = new byte[256];
            string data = null;

            // Get a stream object for reading and writing
            NetworkStream stream = client.GetStream();
            // READ
            Task.Run(async () =>
            {
                int i;
                // Loop to receive all the data sent by the client.
                while ((i = await stream.ReadAsync(bytes, 0, bytes.Length)) != 0)
                {
                    // Translate data bytes to a ASCII string.
                    data = System.Text.Encoding.ASCII.GetString(bytes, 0, i);
                    Console.WriteLine("Received: {0}", data);
                }
            });

            // WRITE
            Task.Run(async () =>
            {
                while (true)
                {
                    if (!string.IsNullOrEmpty(data))
                    {
                        data = data.ToUpper();

                        var msg = System.Text.Encoding.ASCII.GetBytes(data);

                        // Send back a response.
                        await stream.WriteAsync(msg, 0, msg.Length);
                        client.Close();
                        break;
                    }
                }
            });
        }
    }
    class Program
    {
        static void Main(string[] args)
        {
            Task.Run(() =>
            {
                TCPServer.StartReading();
            });

            Console.ReadLine();
        }
    }
}
