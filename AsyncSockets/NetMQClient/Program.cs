using System;
using System.Threading.Tasks;

namespace NetMQClient
{
    class Program
    {
        static void Main(string[] args)
        {
            Task.Factory.StartNew(() =>
            {
                for (var i = 1; i <= 500; i++)
                {
                    var index = i;
                    Task.Factory.StartNew(() => new NetMQClient().StartSending(index));
                }
            });

            Console.WriteLine("Press any key to finish!");
            Console.ReadLine();
        }
    }
}
