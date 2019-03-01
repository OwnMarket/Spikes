using Microsoft.Extensions.Configuration;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace SocketsTest
{
    public static class Config
    {
        static string workingDir = Directory.GetCurrentDirectory();
        static IConfigurationRoot config = new ConfigurationBuilder()
            .SetBasePath(workingDir)
            .AddJsonFile("Config.json")
            .Build();

        public static string ListeningAddress => config["ListeningAddress"];
        public static string PublicAddress => config["PublicAddress"];
        public static List<string> NetworkBootstrapNodes =>
            config.GetSection("NetworkBootstrapNodes").GetChildren()
            .Select(c => c.Value)
            .ToList();
    }
}
