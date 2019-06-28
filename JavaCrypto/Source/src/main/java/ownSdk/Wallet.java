package ownSdk;

public final class Wallet {
    public Wallet() {
        Wallet wallet = Crypto.generateWallet();
        setWallet(wallet);
    }

    public Wallet(String privateKey) {
        Wallet wallet = Crypto.walletFromPrivateKey(privateKey);
        setWallet(wallet);
    }

    public Wallet(String privateKey, String address) {
        setPrivateKey(privateKey);
        setAddress(address);
    }

    private String privateKey;
    private String address;

    public String getPrivateKey() {
        return this.privateKey;
    }

    private void setWallet(Wallet wallet) {
        this.privateKey = wallet.getPrivateKey();
        this.address = wallet.getAddress();
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
