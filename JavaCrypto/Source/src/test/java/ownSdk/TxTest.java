package ownSdk;

import org.junit.Test;

import static org.junit.Assert.*;

public class TxTest {
    
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // TX
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testCreateTxReturnsCorrectJson() {
        Wallet senderWallet = new Wallet();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 123,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": []\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 123);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);
    }

    @Test
    public void testCreateTxExpirationTimeZeroIfNotProvided() {
        Wallet senderWallet = new Wallet();
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": []\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);        
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions: Network Management
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    public void testAddTransferChxAction() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet = new Wallet();
        float amount = 1000;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"TransferChx\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"recipientAddress\": \"%s\",\n", recipientWallet.getAddress()));
        sb.append(String.format("        \"amount\": %4.1f\n", amount));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addTransferChxAction(recipientWallet.getAddress(), amount);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddDelegateStakeAction() {
        Wallet senderWallet = new Wallet();
        Wallet validatorWallet = new Wallet();
        float amount = 100000;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"DelegateStake\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"validatorAddress\": \"%s\",\n", validatorWallet.getAddress()));
        sb.append(String.format("        \"amount\": %4.1f\n", amount));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addDelegateStakeAction(validatorWallet.getAddress(), amount);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddConfigureValidatorAction() {
        Wallet senderWallet = new Wallet();
        String networkAddress = "val01.some.domain.com:25718";
        float sharedRewardPercent = 100000;
        boolean isEnabled = true;        

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"ConfigureValidator\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"networkAddress\": \"%s\",\n", networkAddress));
        sb.append(String.format("        \"sharedRewardPercent\": %6.1f,\n", sharedRewardPercent));
        sb.append(String.format("        \"isEnabled\": %b\n", isEnabled));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addConfigureValidatorAction(networkAddress, sharedRewardPercent, isEnabled);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddRemoveValidatorAction() {
        Wallet senderWallet = new Wallet(); 

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"RemoveValidator\",\n");
        sb.append("      \"actionData\": {}\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addRemoveValidatorAction();
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions: Asset Management
    ////////////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    public void testAddTransferAssetAction() {
        Wallet senderWallet = new Wallet();
        String fromAccountHash = "FAccH1";
        String toAccountHash = "TAccH1";
        String assetHash = "AssetH1";
        float amount = 100;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"TransferAsset\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"fromAccountHash\": \"%s\",\n", fromAccountHash));
        sb.append(String.format("        \"toAccountHash\": \"%s\",\n", toAccountHash));
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"amount\": %3.1f\n", amount));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addTransferAssetAction(fromAccountHash, toAccountHash, assetHash, amount);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddCreateAssetEmissionAction() {
        Wallet senderWallet = new Wallet();
        String emissionAccountHash = "EAccH1";
        String assetHash = "AssetH1";   
        float amount = 10000; 

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"CreateAssetEmission\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"emissionAccountHash\": \"%s\",\n", emissionAccountHash));
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"amount\": %5.1f\n", amount));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addCreateAssetEmissionAction(emissionAccountHash, assetHash, amount);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddCreateAssetAction() {
        Wallet senderWallet = new Wallet();

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"CreateAsset\",\n");
        sb.append("      \"actionData\": {}\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addCreateAssetAction();
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }    

    @Test
    public void testAddCreateAssetActionReturnsAssetHash() {
        Wallet senderWallet = new Wallet();
        long nonce = 1;
        String expected = Crypto.deriveHash(senderWallet.getAddress(), nonce, (short)1);
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        String actual = tx.addCreateAssetAction();
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddSetAssetCodeAction() {
        Wallet senderWallet = new Wallet();        
        String assetHash = "AssetH1";   
        String assetCode = "AST1";

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"SetAssetCode\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"assetCode\": \"%s\"\n", assetCode));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addSetAssetCodeAction(assetHash, assetCode);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }    

    @Test
    public void testAddSetAssetControllerAction() {
        Wallet senderWallet = new Wallet();
        Wallet controllerWallet = new Wallet();
        String assetHash = "AssetH1";   

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"SetAssetController\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"controllerAddress\": \"%s\"\n", controllerWallet.getAddress()));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addSetAssetControllerAction(assetHash, controllerWallet.getAddress());
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }
}
