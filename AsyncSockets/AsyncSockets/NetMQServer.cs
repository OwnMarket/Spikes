using NetMQ;
using NetMQ.Sockets;
using System;

namespace Sockets
{
    public class NetMQServer
    {
        static RouterSocket server;
        public void StartListening()
        {
            Console.WriteLine("Started and listening");
            server = new RouterSocket("@tcp://127.0.0.1:25702");
            server.Options.Backlog = 1000;
            server.ReceiveReady += Server_ReceiveReady;

            using (var poller = new NetMQPoller())
            {
                poller.Add(server);
                poller.RunAsync();
                Console.ReadLine();
            }
        }

        private void Server_ReceiveReady(object sender, NetMQSocketEventArgs e)
        {
            var fromClientMessage = new NetMQMessage();
            while (server.TryReceiveMultipartMessage(ref fromClientMessage))
            {
                var clientAddress = fromClientMessage[0];
                var clientOriginalMessage = fromClientMessage[1].ConvertToString();
                Console.WriteLine("From Client: {0}", clientOriginalMessage);

                var messageToClient = new NetMQMessage();
                messageToClient.Append(clientAddress);
                messageToClient.Append(clientOriginalMessage);
                e.Socket.SendMultipartMessage(messageToClient);
            }
        }
    }
}
