package ownSdk;

public class WalletInfo 
{
    public static WalletInfo Create(String privateKey, String address) {
        WalletInfo wallet = new WalletInfo();
        wallet.PrivateKey = privateKey;
        wallet.Address = address;
        return wallet;
    }
    
    public String PrivateKey;
    public String Address;
}