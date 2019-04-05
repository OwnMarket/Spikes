using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Linq;

namespace LogAnalyzer
{
    class OwnLogAnalyzer
    {
        private readonly string _logsPath;
        private readonly int _nodesCount;
        internal OwnLogAnalyzer (string path, int nodeCount)
        {
            _logsPath = path;
            _nodesCount = nodeCount;
        }

        public bool CreateReport ()
        {
            var txSubmitted = new Dictionary<string, Tuple<DateTime, DateTime>>();
            var txReceived = new Dictionary<string, Tuple<DateTime, DateTime>>();

            for (var nodeIndex = 1; nodeIndex <= _nodesCount; nodeIndex++)
            {
                ParseNodeLog(txSubmitted, $"node{nodeIndex}.log", "TxSubmitted");
                ParseNodeLog(txReceived, $"node{nodeIndex}.log", "TxReceived");
            }

            var aggregated = new Dictionary<string, Tuple<TimeSpan, DateTime>>();
            foreach (var (txHash, sentTs) in txSubmitted)
            {
                if (txReceived.TryGetValue(txHash, out Tuple<DateTime, DateTime> receveidTs))
                    aggregated[txHash] = new Tuple<TimeSpan, DateTime>(receveidTs.Item1 - sentTs.Item1, sentTs.Item2);
            }

            var report = aggregated.ToList().OrderByDescending(kvp => kvp.Value.Item1);
            var lines = report.Select(r => $"TxHash : {r.Key} Submitted at {r.Value.Item2} Received after {r.Value.Item1}");

            File.WriteAllLines(Path.Combine(_logsPath, "report.txt"), lines);

            return true;
        }

        private void ParseNodeLog(Dictionary<string, Tuple<DateTime, DateTime>> txCollection, string fileName, string parseText)
        {
            var nodeFilePath = Path.Combine(_logsPath, fileName);
            var lines = File.ReadAllLines(nodeFilePath);

            foreach (var line in lines.Where(l => l.Contains(parseText)))
            {
                var tokens = line.Replace($"INF | EVENT: {parseText}: ", "").Split(" ");
                var date = DateTime.Parse(tokens[1]);
                var timestamp = DateTime.ParseExact(tokens[1], "HH:mm:ss.fff", CultureInfo.InvariantCulture);
                if (txCollection.TryGetValue(tokens[2], out Tuple<DateTime, DateTime> val) && val.Item1 > timestamp)
                    continue;

                txCollection[tokens[2]] = new Tuple<DateTime, DateTime>(timestamp, date);
            }
        }
    }
}
