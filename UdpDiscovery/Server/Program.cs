using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace UdpDiscovery
{
    class Program
    {
        static void Main(string[] args)
        {
            int PORT = 9875;
            UdpClient udpClient = new UdpClient();
            udpClient.Client.Bind(new IPEndPoint(IPAddress.Any, PORT));

            var from = new IPEndPoint(0, 0);
            Task.Run(() =>
            {
                while (true)
                {
                    var recvBuffer = udpClient.Receive(ref from);

                    Console.WriteLine(Encoding.UTF8.GetString(recvBuffer));

                    if (recvBuffer.Any())
                        udpClient.Send(recvBuffer, recvBuffer.Length, "127.0.0.1", 9876);
                }
            });

            Console.ReadLine();
        }
    }
}
