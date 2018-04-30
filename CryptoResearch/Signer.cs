using System;
using System.Text;
using Org.BouncyCastle.Asn1.Sec;
using Org.BouncyCastle.Asn1.X9;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Math.EC;
using Org.BouncyCastle.Security;
using Org.BouncyCastle.Crypto.Signers;

namespace ChainiumWallets
{
    class Signer
    {
        private static X9ECParameters curve = SecNamedCurves.GetByName("secp256k1");
        private static ECDomainParameters domain = new ECDomainParameters(curve.Curve, curve.G, curve.N, curve.H);

        public static BigInteger[] GenerateSignature(byte[] message, BigInteger privateKey)
        {
             ECPrivateKeyParameters privateKeyParameters = new ECPrivateKeyParameters(privateKey, domain);

             SecureRandom k = new SecureRandom();
                        
             ParametersWithRandom param = new ParametersWithRandom(privateKeyParameters, k);

             ECDsaSigner ecdsa = new ECDsaSigner();

             ecdsa.Init(true, param);

             BigInteger[] signature = ecdsa.GenerateSignature(message);

             return signature;

        }
    }
}

