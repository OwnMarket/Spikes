using NetMQ;
using NetMQ.Sockets;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;

namespace NetMQClient
{
    public class NetMQClient
    {
        string _id;
        DealerSocket client = new DealerSocket(">tcp://127.0.0.1:25702");
        NetMQQueue<NetMQMessage> messageQueue = new NetMQQueue<NetMQMessage>();
        public NetMQClient (string id)
        {
            _id = id;
            client.Options.Identity = Encoding.Unicode.GetBytes(_id);
            //client.ReceiveReady += Client_ReceiveReady; // Use busy waiting instead of event driven handlking due to congestion of CPU
            messageQueue.ReceiveReady += MessageQueue_ReceiveReady;
            Task.Factory.StartNew(() => ReceiveMessage(client));
        }

        private void ReceiveMessage(DealerSocket client)
        {
            while (true)
            {
                var hasmore = false;
                client.ReceiveFrameString(out hasmore);
                if (hasmore)
                {
                    var result = client.ReceiveFrameString(out hasmore);
                    Console.WriteLine("REPLY {0}", result);
                }
            }
        }

        public async Task StartSending()
        {
            var message = string.Format("Id = {0}", _id);
            var bytes = Encoding.ASCII.GetBytes(message);


            using (var poller = new NetMQPoller())
            {
                poller.Add(client);
                poller.Add(messageQueue);
                poller.RunAsync();

                for (int i = 0; i < 1000; i++)
                {
                    var messageToServer = new NetMQMessage();
                    messageToServer.AppendEmptyFrame();
                    messageToServer.Append(message);
                    messageQueue.Enqueue(messageToServer);
                }
            }

            Console.WriteLine("!!!!!!!!!!!!!!!!!!!!!");
        }

        private void Client_ReceiveReady(object sender, NetMQSocketEventArgs e)
        {
            var hasmore = false;
            e.Socket.ReceiveFrameString(out hasmore);
            if (hasmore)
            {
                var result = e.Socket.ReceiveFrameString(out hasmore);
                Console.WriteLine("REPLY {0}", result);
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
