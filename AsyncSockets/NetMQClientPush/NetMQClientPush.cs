using NetMQ;
using NetMQ.Sockets;
using System;
using System.Collections.Generic;
using System.Text;
using System.Threading.Tasks;


namespace NetMQClientPush
{
    public class NetMQClientPush
    {
        int _id;
        PushSocket socket = new PushSocket(">tcp://127.0.0.1:1234");
        public NetMQClientPush(int id)
        {
            _id = id;
        }

        public async Task StartPushing()
        {
            var message = string.Format("Id = {0}", _id);
            var bytes = Encoding.ASCII.GetBytes(message);
            while (true)
            {
                socket.SendFrame(message);
                Console.WriteLine(" {0} Sent message", _id);
                //await Task.Delay(_id * 10);

                string fromServerMessage = socket.ReceiveFrameString();
                Console.WriteLine("Client {0} From Server: {1}", _id, fromServerMessage);
                if (!fromServerMessage.Contains(_id.ToString()))
                    throw new InvalidOperationException(string.Format("{0} Received {1}", _id, fromServerMessage));
            }
        }
    }
}
