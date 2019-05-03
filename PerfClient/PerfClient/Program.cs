using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;

namespace PerfClient
{
    class Program
    {
        private static readonly HttpClient client = new HttpClient();

        static void Main(string[] args)
        {
            if (args.Length != 1)
            {
                Console.WriteLine("Usage: PerfClient script.sh");
            }
            else
            {
                var filePath = args[0];
                var client = new HttpClient();
                client.DefaultRequestHeaders.Accept.Clear();
                client.DefaultRequestHeaders.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));
                var lines = File.ReadAllLines(filePath).Where(line => !line.StartsWith("#")).ToArray();
                var startIndex = lines[0].IndexOf("@-") + 3;
                var endIndex = lines[0].IndexOf("/tx", startIndex);
                var url = lines[0].Substring(startIndex, endIndex - startIndex);
                client.BaseAddress = new Uri(url);

                var jsons = new List<string>();
                for (var i = 0; i < lines.Length; i += 3)
                {
                    var json = lines[i + 1];
                    jsons.Add(lines[i + 1]);

                }
                foreach(var json in jsons.AsParallel())
                {
                    var content = new StringContent(json, Encoding.UTF8, "application/json");
                    var res = client.PostAsync("/tx", content).Result;
                }

                Console.WriteLine("Success!!");
            }
        }
    }
}
