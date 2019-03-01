using NetMQ;
using NetMQ.Sockets;
using System;

namespace Sockets
{
    public class NetMQServer
    {
        static RouterSocket server;
        NetMQQueue<NetMQMessage> repliesQueue = new NetMQQueue<NetMQMessage>();
        public void StartListening()
        {
            Console.WriteLine("Started and listening");
            server = new RouterSocket("@tcp://*:25702");
            server.ReceiveReady += Server_ReceiveReady1;
            repliesQueue.ReceiveReady += RepliesQueue_ReceiveReady;
            using (var poller = new NetMQPoller())
            {
                poller.Add(server);
                poller.Add(repliesQueue);
                poller.RunAsync();
                Console.ReadLine();
            }
        }

        private void RepliesQueue_ReceiveReady(object sender, NetMQQueueEventArgs<NetMQMessage> e)
        {
            while (e.Queue.TryDequeue(out NetMQMessage messageToClient, TimeSpan.FromMilliseconds(10)))
            {
                server.SendMultipartMessage(messageToClient);
                Console.WriteLine("Sent back to client");
            }
        }

        private void Server_ReceiveReady1(object sender, NetMQSocketEventArgs e)
        {
            var fromClientMessage = server.ReceiveMultipartMessage();
            var clientAddress = fromClientMessage[0];
            var clientOriginalMessage = fromClientMessage[2].ConvertToString();
            Console.WriteLine("From Client: {0}", clientOriginalMessage);

            System.Threading.Thread.Sleep(500);

            var messageToClient = new NetMQMessage();
            messageToClient.Append(clientAddress);
            messageToClient.AppendEmptyFrame();
            messageToClient.Append(clientOriginalMessage);
            repliesQueue.Enqueue(messageToClient);

        }
    }
}
