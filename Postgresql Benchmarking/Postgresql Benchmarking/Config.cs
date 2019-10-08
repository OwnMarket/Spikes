using System.IO;
using Microsoft.Extensions.Configuration;

namespace Postgresql.Benchmarking
{
    public static class Config
    {
        public static string _workingDir = 
            Directory.GetCurrentDirectory();

        public static IConfigurationRoot config =
             new ConfigurationBuilder()
            .SetBasePath(_workingDir)
            .AddJsonFile("Config.json")
            .Build();

        public static string DbConnectionString =>
            config["DbConnectionString"];
    }
}
