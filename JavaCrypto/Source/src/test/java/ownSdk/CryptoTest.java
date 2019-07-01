package ownSdk;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit test for simple App.
 */
public class CryptoTest {

    @Test
    public void testEncodingBase58() {
        String originalData = "Chainium";
        String expected = "CGwVR5Wyya4";
        String actual = Crypto.encode58(originalData.getBytes());
        String decoded = new String(Crypto.decode58(actual));
        assertEquals(expected, actual);
        assertEquals(originalData, decoded);
    }

    @Test
    public void testEncodingBase64() {
        String originalData = "Chainium";
        String expected = "Q2hhaW5pdW0=";
        String actual = Crypto.encode64(originalData.getBytes());
        String decoded = new String(Crypto.decode64(actual));
        assertEquals(expected, actual);
        assertEquals(originalData, decoded);
    }

    @Test
    public void testHashing() {
        String originalData = "Chainium";
        String expected = "Dp6vNLdUbRTc1Y3i9uSBritNqvqe4es9MjjGrVi1nQMu";
        String actual = Crypto.hash(originalData.getBytes());
        assertEquals(expected, actual);
    }

    @Test
    public void testDeriveHash() {
        String address = "CHPJ6aVwpGBRf1dv6Ey1TuhJzt1VtCP5LYB";
        long nonce = 32;
        short txActionNumber = 2;
        String expected = "5kHcMrwXUptjmbdR8XBW2yY3FkSFwnMdrVr22Yg39pTR";
        String actual = Crypto.deriveHash(address, nonce, txActionNumber);
        assertEquals(expected, actual);
    }

    @Test
    public void testAddressFromPrivateKey() {
        String privateKey = "3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq";
        String expected = "CHGmdQdHfLPcMHtzyDzxAkTAQiRvKJrkYv8";
        String actual = Crypto.addressFromPrivateKey(privateKey);
        assertEquals(expected, actual);
    }

    @Test
    public void testSigninigTx() {
        String privateKey = "3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq";
        String networkCode = "UNIT_TESTS";
        String message = "Chainium";
        String expected = "EYzWMyZjqHkwsNFKcFEg4Q64m4jSUD7cAeKucyZ3a9MKeNmXTbRK3czqNVGj9RpkPGji9AtGiUxDtipqE3DtFPHxU";
        String actual = Crypto.signMessage(networkCode, privateKey, message);
        assertEquals(expected, actual);
    }

    @Test
    public void testSigninigPlainText() {
        String message = "Chainium";
        String privateKey = "3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq";
        String expected = "EzCsWgPozyVT9o6TycYV6q1n4YK4QWixa6Lk4GFvwrj6RU3K1wHcwNPZJUMBYcsGp5oFhytHiThon5zqE8uLk8naB";
        String actual = Crypto.signPlainText(privateKey, message);
        assertEquals(expected, actual);
    }    

    @Test
    public void testVerifyPlainTextSignature() {
        String message = "Chainium";
        Wallet wallet = Crypto.generateWallet();
        String expected = Crypto.addressFromPrivateKey(wallet.getPrivateKey());
        String signature = Crypto.signPlainText(wallet.getPrivateKey(), message);
        String actual = Crypto.verifyPlainTextSignature(signature, message);
        assertEquals(expected, actual);
    }    
}
