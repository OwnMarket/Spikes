using SimpleBase;
using System.Security.Cryptography;

namespace Postgresql.Benchmarking
{
    public static class Crypto
    {
        private static byte[] Sha256(byte[] bytes)
        {
            var sha256 = SHA256.Create();
            return sha256.ComputeHash(bytes);
        }
        private static string Encode(byte[] bytes)
        {
            return Base58.Bitcoin.Encode(bytes);
        }

        public static string Hash(byte[] data)
        {
            return Encode(Sha256(data));
        }
    }
}
