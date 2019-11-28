using System;
using System.Net.Sockets;
using System.Threading.Tasks;

namespace Client
{

    class TCPClient
    {
        public static async Task Connect(int id)
        {
            try
            {
                // Create a TcpClient.
                // Note, for this client to work you need to have a TcpServer
                // connected to the same address as specified by the server, port
                // combination.
                Int32 port = 13000;
                byte[] data;
                using (var client = new TcpClient("127.0.0.1", port))
                {
                    // Translate the passed message into ASCII and store it as a Byte array.
                    data = System.Text.Encoding.ASCII.GetBytes(id.ToString());

                    // Get a client stream for reading and writing.
                    var stream = client.GetStream();

                    // Send the message to the connected TcpServer.
                    await stream.WriteAsync(data, 0, data.Length);
                    Console.WriteLine("Sent: {0}", id);

                    await Task.Run(async () =>
                    {
                        // Buffer to store the response bytes.
                        data = new byte[256];

                        // String to store the response ASCII representation.
                        var responseData = string.Empty;

                        // Read the first batch of the TcpServer response bytes.
                        var bytes = await stream.ReadAsync(data, 0, data.Length);
                        responseData = System.Text.Encoding.ASCII.GetString(data, 0, bytes);
                        Console.WriteLine("Received: {0}", responseData);
                    });
                }
            }
            catch (ArgumentNullException e)
            {
                Console.WriteLine("ArgumentNullException: {0}", e);
            }
            catch (SocketException e)
            {
                Console.WriteLine("SocketException: {0}", e);
            }

            //Console.WriteLine("\n Press Enter to continue...");
            //Console.Read();
        }
    }
    class Program
    {
        static void Main(string[] args)
        {
            Task.Run(() =>
                {
                    for (var i = 1; i <= 10000; i++)
                    {
                        var index = i;
                        Task.Run(() => TCPClient.Connect(index));
                    }
                }
            );
            Console.ReadLine();
        }
    }
}
