using System;
using System.Text;
using Org.BouncyCastle.Asn1.Sec;
using Org.BouncyCastle.Asn1.X9;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Math.EC;
using Org.BouncyCastle.Security;

namespace ChainiumWallets
{
    class Program
    {
        static void Main(string[] args)
        {
            string method = args[0];

            if (method == "generate")
            {
                string passphrase = args[1];
                byte[] seed = Encoding.ASCII.GetBytes(passphrase);

                string[] keyPair = Wallet.GenerateKeys(seed);

                Console.WriteLine("Private key is     " + keyPair[0]);
                Console.WriteLine("Public key is      " + keyPair[1]);
            }
            else if (method == "sign")
            {
                string plainMessage = args[1];
                byte[] message = Encoding.ASCII.GetBytes(plainMessage);

                BigInteger privateKey = new BigInteger(args[2], 16);

                BigInteger[] signature = Signer.GenerateSignature(message, privateKey);

                Console.WriteLine("Signature component R is " + signature[0].ToString(16));
                Console.WriteLine("Signature component S is " + signature[1].ToString(16));
            }
            else if (method == "verify")
            {
                string plainMessage = args[1];
                byte[] message = Encoding.ASCII.GetBytes(plainMessage);

                BigInteger signatureR = new BigInteger(args[2], 16);
                BigInteger signatureS = new BigInteger(args[3], 16);

                byte[] publicKey = new BigInteger(args[4], 16).ToByteArray();

                Boolean signatureVerified = Verifier.VerifySignature(message, signatureR, signatureS, publicKey);

                Console.WriteLine("Signature is valid? " + signatureVerified);
            }
        }
    }

}

