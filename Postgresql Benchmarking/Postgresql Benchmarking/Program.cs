using System;
using System.Text;

namespace Postgresql.Benchmarking
{
    class Program
    {
        static void Main(string[] args)
        {
            DbTools.InitFirebird(Config.DbConnectionString);
            var content = Encoding.UTF8.GetBytes(Utils.RandomString(1000));
            for (var it = 1 ; it < 100; it ++)
            {
                var startTime = DateTime.UtcNow;
                var min = (it - 1) * 100000 + 1;
                var max = it * 100000;

                Console.WriteLine($"Iteration {it} range ({min}, {max})");
                for (var row = min; row < max; row ++)
                {
                    Console.Write($"{row}\r");
                    var bytes = BitConverter.GetBytes(row);
                    var hash = Crypto.Hash(bytes);
                    Db.SetValue(DbEngineType.Firebird, hash, content);
                }

                Console.WriteLine();
                var endTime = DateTime.UtcNow;
                var elapsed = (endTime - startTime).TotalSeconds;
                Console.WriteLine($"Elapsed Generation (Iteration {it}) = {elapsed} (s)");

                var randomRow = new Random().Next(min, max);
                var randomHash = Crypto.Hash(BitConverter.GetBytes(randomRow));

                startTime = DateTime.UtcNow;
                var value = Db.GetValue(DbEngineType.Firebird, randomHash);
                endTime = DateTime.UtcNow;
                elapsed = (endTime - startTime).TotalMilliseconds;

                Console.WriteLine($"Elapsed GetRandomHash (Iteration {it}) = {elapsed} (ms)");
                Console.WriteLine("=========================================================");
            }

            Console.ReadLine();
        }
    }
}
