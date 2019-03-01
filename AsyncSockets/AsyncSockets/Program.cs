using Sockets;
using System;
using System.Threading.Tasks;

namespace SocketsTest
{
    class Program
    {
        static void Main(string[] args)
        {
            var server = new NetMQServer();
            Task.Factory.StartNew(() => server.StartListening());
            Console.ReadLine();
        }
    }
}
