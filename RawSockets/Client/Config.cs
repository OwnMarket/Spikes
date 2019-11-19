using Microsoft.Extensions.Configuration;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;

namespace Client
{
    public static class Config
    {
        static string workingDir = Directory.GetCurrentDirectory();
        static IConfigurationRoot config = new ConfigurationBuilder()
            .SetBasePath(workingDir)
            .AddJsonFile("Config.json")
            .Build();

        public static int MaxClients => Int32.Parse(config["MaxClients"]);
    }
}
