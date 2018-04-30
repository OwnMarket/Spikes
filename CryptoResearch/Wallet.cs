using System;
using System.Text;
using Org.BouncyCastle.Asn1.Sec;
using Org.BouncyCastle.Asn1.X9;
using Org.BouncyCastle.Crypto;
using Org.BouncyCastle.Crypto.Parameters;
using Org.BouncyCastle.Crypto.Generators;
using Org.BouncyCastle.Math;
using Org.BouncyCastle.Math.EC;
using Org.BouncyCastle.Security;

namespace ChainiumWallets
{
    class Wallet
    {
        private static X9ECParameters curve = SecNamedCurves.GetByName("secp256k1");
        private static ECDomainParameters domain = new ECDomainParameters(curve.Curve, curve.G, curve.N, curve.H);

        public static string[] GenerateKeys(byte[] seed)
        {
            ECKeyPairGenerator gen = new ECKeyPairGenerator();

            var secureRandom = new SecureRandom();
            secureRandom.SetSeed(seed);
            var keyGenParam = new ECKeyGenerationParameters(domain, secureRandom);
            gen.Init(keyGenParam);

            AsymmetricCipherKeyPair keyPair = gen.GenerateKeyPair();

            ECPrivateKeyParameters privateParams = (ECPrivateKeyParameters)keyPair.Private;
            ECPublicKeyParameters publicParams = (ECPublicKeyParameters)keyPair.Public;

            BigInteger privateKey = privateParams.D;
            BigInteger publicKey = new BigInteger(publicParams.Q.GetEncoded());

            BigInteger publicKeyCompressed = new BigInteger(publicParams.Q.GetEncoded(true));

            string[] walletKeyPair = new string[2];

            walletKeyPair[0] = privateKey.ToString(16);
            walletKeyPair[1] = publicKey.ToString(16);

            return walletKeyPair;
        }
    }
}

