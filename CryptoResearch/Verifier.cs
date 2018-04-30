using System;
using System.Text;
using Org.BouncyCastle.Asn1.Sec;
using Org.BouncyCastle.Asn1.X9;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Math.EC;
using Org.BouncyCastle.Crypto.Signers;

namespace ChainiumWallets
{
    class Verifier
    {
        private static X9ECParameters curve = SecNamedCurves.GetByName("secp256k1");
        private static ECDomainParameters domain = new ECDomainParameters(curve.Curve, curve.G, curve.N, curve.H);

        public static Boolean VerifySignature(byte[] message, BigInteger messageHash, BigInteger messageSignature, byte[] publicKey)
        {
            ECPublicKeyParameters publicKeyParameters = new ECPublicKeyParameters(domain.Curve.DecodePoint(publicKey), domain);

            ECDsaSigner ecdsa = new ECDsaSigner();

            ecdsa.Init(false, publicKeyParameters);

            return ecdsa.VerifySignature(message, messageHash, messageSignature);

        }
    }
}

