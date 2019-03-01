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
            server = new RouterSocket("@tcp://*:25702");
            server.ReceiveReady += Server_ReceiveReady1;

            using (var poller = new NetMQPoller())
            {
                poller.Add(server);
                poller.RunAsync();
                Console.ReadLine();
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
            e.Socket.SendMultipartMessage(messageToClient);
            Console.WriteLine("Sent back to client");
        }
    }
}
