using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;

namespace LogAnalyzer
{
    class Program
    {
        private static string logsPath = @"C:\temp\node";
        private static int nodesCount = 4;

        static void Main(string[] args)
        {
            if (args.Length == 2)
            {
                logsPath = args[0];
                if (int.TryParse(args[1], out int count))
                    nodesCount = count;
            }

            var analyzer = new OwnLogAnalyzer(logsPath, nodesCount);
            analyzer.CreateReport();
            Console.WriteLine("Report successfully generated!");
            Console.ReadLine();
        }
    }
}
