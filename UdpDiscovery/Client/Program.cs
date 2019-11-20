using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Client
{
    class Program
    {
        static void Main(string[] args)
        {
            int PORT = 9875;
            for (var i = 1; i <= 1000; i ++ )
            {
                var data = Encoding.UTF8.GetBytes(i.ToString());
                UdpClient udpClient = new UdpClient();
                udpClient.Client.Bind(new IPEndPoint(IPAddress.Any, 9875 + i));

                var from = new IPEndPoint(0, 0);
                Task.Run(() =>
                {
                    while (true)
                    {
                        var recvBuffer = udpClient.Receive(ref from);
                        Console.WriteLine(Encoding.UTF8.GetString(recvBuffer));
                    }
                });

                udpClient.Send(data, data.Length, "127.0.0.1", PORT);
            }

            Console.ReadLine();
        }
    }
}
