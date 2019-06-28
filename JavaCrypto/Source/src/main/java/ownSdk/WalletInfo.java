package ownSdk;

public final class WalletInfo 
{
    public static WalletInfo create(String privateKey, String address) {
        WalletInfo wallet = new WalletInfo();
        wallet.setPrivateKey(privateKey);
        wallet.setAddress(address);
        return wallet;
    }
    
    private String privateKey;
    private String address;

    public String getPrivateKey() {
        return this.privateKey;
    }

    private void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getAddress() {
        return this.address;
    }

    private void setAddress(String address){
        this.address = address;
    }
}