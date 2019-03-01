using System;
using System.Threading.Tasks;

namespace NetMQClient
{
    class Program
    {
        static void Main(string[] args)
        {
            var client1 = new NetMQClient(args[0]);
            Task.Factory.StartNew(() => client1.StartSending());
            Console.ReadLine();

            Console.WriteLine("Hello World!");
        }
    }
}
