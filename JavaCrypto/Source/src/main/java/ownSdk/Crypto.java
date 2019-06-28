package ownSdk;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.apache.commons.lang3.ArrayUtils;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ECKey.ECDSASignature;
import org.bouncycastle.util.Arrays;
import org.bitcoinj.core.Sha256Hash;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;

public final class Crypto {

    private Crypto() {
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Encoding
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public String Encode64(byte[] src) {
        return Base64.getEncoder().encodeToString(src);
    }

    public byte[] Decode64(String src) {
        return Base64.getDecoder().decode(src);
    }

    public String Encode58(byte[] src) {
        return Base58.encode(src);
    }

    public byte[] Decode58(String src) {
        return Base58.decode(src);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Hashing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private byte[] hash(byte[] data, String algorithm) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(data, 0, data.length);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] Sha256(byte[] data) {
        return hash(data, "SHA-256");
    }

    public byte[] Sha512(byte[] data) {
        return hash(data, "SHA-512");
    }

    public byte[] Sha160(byte[] data) {
        byte[] sha512 = Sha512(data);
        return Arrays.copyOfRange(sha512, 0, 20);
    }

    public String Hash(byte[] data) {
        return Encode58(Sha256(data));
    }

    private byte[] shortToBytes(short value) {
        return new byte[] { (byte) ((value >> 8) & 0xFF), (byte) (value & 0xFF) };
    }

    private byte[] longToBytes(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);
        return buffer.array();
    }

    public String DeriveHash(String address, long nonce, short txActionNumber) {
        byte[] addressBytes = Decode58(address);
        byte[] nonceBytes = longToBytes(nonce);
        byte[] txActionNumberBytes = shortToBytes(txActionNumber);
        byte[] concatBytes = ArrayUtils.addAll(ArrayUtils.addAll(addressBytes, nonceBytes), txActionNumberBytes);
        return Hash(concatBytes);
    }

    private String blockchainAddress(byte[] publicKey) {
        byte[] addressPrefix = new byte[] { 6, 90 };
        byte[] sha160_256 = Sha160(Sha256(publicKey));
        byte[] publicKeyHashWithPrefix = ArrayUtils.addAll(addressPrefix, sha160_256);
        byte[] sha256_256 = Sha256(Sha256(publicKeyHashWithPrefix));
        byte[] checksum = Arrays.copyOfRange(sha256_256, 0, 4);
        return Encode58(ArrayUtils.addAll(publicKeyHashWithPrefix, checksum));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Signing
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public WalletInfo GenerateWallet() {
        ECKey ecKey = new ECKey();
        String privateKey = Encode58(ecKey.getPrivKeyBytes());
        String address = blockchainAddress(ecKey.getPubKey());
        return WalletInfo.Create(privateKey, address);
    }

    public String AddressFromPrivateKey(String privateKey) {
        byte[] privateKeyBytes = Decode58(privateKey);
        ECKey ecKey = ECKey.fromPrivate(privateKeyBytes).decompress();
        return blockchainAddress(ecKey.getPubKey());
    }

    public WalletInfo WalletFromPrivateKey(String privateKey) {
        String address = AddressFromPrivateKey(privateKey);
        return WalletInfo.Create(privateKey, address);
    }

    private String sign(String privateKey, byte[] dataHash) {
        byte[] privateKeyBytes = Decode58(privateKey);
        ECKeyPair keyPair = ECKeyPair.create(new BigInteger(1, privateKeyBytes));

        Sign.SignatureData signature = Sign.signMessage(dataHash, keyPair, false);

        byte[] signatureBytes = ArrayUtils.addAll(signature.getR(), signature.getS());
        int recoveryId = signature.getV() - 27;
        signatureBytes = ArrayUtils.add(signatureBytes, (byte) recoveryId);

        return Encode58(signatureBytes);
    }

    public String SignMessage(String networkCode, String privateKey, String message) {
        byte[] messageHash = Sha256(message.getBytes());
        byte[] networkIdBytes = Sha256(networkCode.getBytes());
        byte[] dataToSign = Sha256(ArrayUtils.addAll(messageHash, networkIdBytes));
        return sign(privateKey, dataToSign);
    }

    public String SignPlainText(String privateKey, String text) {
        byte[] dataToSign = Sha256(text.getBytes());
        return sign(privateKey, dataToSign);
    }

    public String VerifyPlainTextSignature(String signature, String text) {
        byte[] dataToVerify = Sha256(text.getBytes());
        byte[] signatureBytes = Decode58(signature);

        byte[] r = Arrays.copyOfRange(signatureBytes, 0, 32);
        byte[] s = Arrays.copyOfRange(signatureBytes, 32, 64);
        int recoveryId = signatureBytes[64];

        ECDSASignature ecdsaSig = new ECDSASignature(new BigInteger(1, r), new BigInteger(1, s));

        ECKey ecKey = ECKey.recoverFromSignature(recoveryId, ecdsaSig, Sha256Hash.wrap(dataToVerify), false);
        return blockchainAddress(ecKey.getPubKey());
    }

    public static void main(String[] args) {
        Crypto ownSdk = new Crypto();
        System.out.println("==================== Encoding ====================");
        String originalData = "Chainium";
        String expected = "CGwVR5Wyya4";
        String actual = ownSdk.Encode58(originalData.getBytes());
        String decoded = new String(ownSdk.Decode58(actual));
        System.out.println(String.format("Expected = %s", expected));
        System.out.println(String.format("Actual = %s", actual));
        System.out.println(String.format("Decoded = %s", decoded));

        System.out.println("==================== Hashing ====================");
        expected = "Dp6vNLdUbRTc1Y3i9uSBritNqvqe4es9MjjGrVi1nQMu";
        actual = ownSdk.Hash(originalData.getBytes());
        System.out.println(String.format("Expected = %s", expected));
        System.out.println(String.format("Actual = %s", actual));

        String address = "CHPJ6aVwpGBRf1dv6Ey1TuhJzt1VtCP5LYB";
        long nonce = 32;
        short txActionNumber = 2;
        expected = "5kHcMrwXUptjmbdR8XBW2yY3FkSFwnMdrVr22Yg39pTR";
        actual = ownSdk.DeriveHash(address, nonce, txActionNumber);
        System.out.println(String.format("Expected = %s", expected));
        System.out.println(String.format("Actual = %s", actual));

        System.out.println("==================== Signing (Address from PrivateKey) ====================");
        String privateKey = "3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq";
        expected = "CHGmdQdHfLPcMHtzyDzxAkTAQiRvKJrkYv8";
        actual = ownSdk.AddressFromPrivateKey(privateKey);
        System.out.println(String.format("Expected = %s", expected));
        System.out.println(String.format("Actual = %s", actual));

        System.out.println("==================== Signing (Tx) ====================");
        String networkCode = "UNIT_TESTS";
        String message = "Chainium";
        expected = "EYzWMyZjqHkwsNFKcFEg4Q64m4jSUD7cAeKucyZ3a9MKeNmXTbRK3czqNVGj9RpkPGji9AtGiUxDtipqE3DtFPHxU";
        actual = ownSdk.SignMessage(networkCode, privateKey, message);
        System.out.println(String.format("Expected = %s", expected));
        System.out.println(String.format("Actual = %s", actual));

        System.out.println("==================== Signing (PlainText) ====================");
        expected = "EzCsWgPozyVT9o6TycYV6q1n4YK4QWixa6Lk4GFvwrj6RU3K1wHcwNPZJUMBYcsGp5oFhytHiThon5zqE8uLk8naB";
        actual = ownSdk.SignPlainText(privateKey, message);
        System.out.println(String.format("Expected = %s", expected));
        System.out.println(String.format("Actual = %s", actual));

        System.out.println("==================== Signing (VerifyPlainText) ====================");
        WalletInfo wallet = ownSdk.GenerateWallet();
        expected = ownSdk.AddressFromPrivateKey(wallet.PrivateKey);
        String signature = ownSdk.SignPlainText(wallet.PrivateKey, message);
        actual = ownSdk.VerifyPlainTextSignature(signature, message);
        System.out.println(String.format("Expected = %s", expected));
        System.out.println(String.format("Actual = %s", actual));

    }
}
