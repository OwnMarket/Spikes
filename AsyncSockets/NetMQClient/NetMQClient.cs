using NetMQ;
using NetMQ.Sockets;
using System;
using System.Text;
using System.Threading;

namespace NetMQClient
{
    public class NetMQClient
    {
        DealerSocket client = new DealerSocket("tcp://127.0.0.1:25702");
        NetMQQueue<NetMQMessage> messageQueue = new NetMQQueue<NetMQMessage>();
        public NetMQClient ()
        {
            messageQueue.ReceiveReady += MessageQueue_ReceiveReady;
            client.Options.Backlog = 1000;
            client.ReceiveReady += Client_ReceiveReady;
        }

        private void Client_ReceiveReady(object sender, NetMQSocketEventArgs e)
        {
            while (e.Socket.TryReceiveFrameString(out string msg))
            {
                Console.WriteLine("REPLY From Server{0}", msg);
            }

        }

        public void StartSending(int id)
        {
            client.Options.Identity = Encoding.Unicode.GetBytes(id.ToString());
            var message = string.Format("Id = {0}", id.ToString());
            var bytes = Encoding.ASCII.GetBytes(message);


            using (var poller = new NetMQPoller())
            {
                poller.Add(client);
                poller.Add(messageQueue);
                poller.RunAsync();

                var messageToServer = new NetMQMessage();
                messageToServer.Append(message);
                messageQueue.Enqueue(messageToServer);
            }
        }

        private void MessageQueue_ReceiveReady(object sender, NetMQQueueEventArgs<NetMQMessage> e)
        {
            while (e.Queue.TryDequeue(out NetMQMessage messageToServer, TimeSpan.FromMilliseconds(10)))
            {
                client.SendMultipartMessage(messageToServer);
            }
        }
    }
}
