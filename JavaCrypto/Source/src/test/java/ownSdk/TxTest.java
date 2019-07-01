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

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions: Account Management
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    
    @Test
    public void testAddCreateAccountActionReturnsAccountHash() {
        Wallet senderWallet = new Wallet();
        long nonce = 1;
        String expected = Crypto.deriveHash(senderWallet.getAddress(), nonce, (short)1);
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        String actual = tx.addCreateAccountAction();
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddSetAccountControllerAction() {
        Wallet senderWallet = new Wallet();
        Wallet controllerWallet = new Wallet();
        String accountHash = "AccountH1";

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"SetAccountController\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"accountHash\": \"%s\",\n", accountHash));
        sb.append(String.format("        \"controllerAddress\": \"%s\"\n", controllerWallet.getAddress()));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addSetAccountControllerAction(accountHash, controllerWallet.getAddress());
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions: Voting
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testAddSubmitVoteAction() {
        Wallet senderWallet = new Wallet();
        String accountHash = "AccountH1";
        String assetHash = "AssetH1";
        String resolutionHash = "ResolutionH1";
        String voteHash = "VoteH1";

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"SubmitVote\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"accountHash\": \"%s\",\n", accountHash));
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"resolutionHash\": \"%s\",\n", resolutionHash));
        sb.append(String.format("        \"voteHash\": \"%s\"\n", voteHash));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addSubmitVoteAction(accountHash, assetHash, resolutionHash, voteHash);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddSubmitVoteWeightAction() {
        Wallet senderWallet = new Wallet();
        String accountHash = "AccountH1";
        String assetHash = "AssetH1";
        String resolutionHash = "ResolutionH1";
        float voteWeight = 12345;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"SubmitVoteWeight\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"accountHash\": \"%s\",\n", accountHash));
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"resolutionHash\": \"%s\",\n", resolutionHash));
        sb.append(String.format("        \"voteWeight\": %5.1f\n", voteWeight));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addSubmitVoteWeightAction(accountHash, assetHash, resolutionHash, voteWeight);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }    

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions: Eligibility
    ////////////////////////////////////////////////////////////////////////////////////////////////////    

    @Test
    public void testAddSetAccountEligibilityAction() {
        Wallet senderWallet = new Wallet();
        String accountHash = "FAccH1";
        String assetHash = "AssetH1";
        boolean isPrimaryEligible = false;
        boolean isSecondaryEligible = true;
    
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"SetAccountEligibility\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"accountHash\": \"%s\",\n", accountHash));
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"isPrimaryEligible\": %b,\n", isPrimaryEligible));
        sb.append(String.format("        \"isSecondaryEligible\": %b\n", isSecondaryEligible));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addSetAccountEligibilityAction(accountHash, assetHash, isPrimaryEligible, isSecondaryEligible);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddSetAssetEligibilityAction() {
        Wallet senderWallet = new Wallet();
        String assetHash = "AssetH1";
        boolean isEligibilityRequired = true;

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"SetAssetEligibility\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"isEligibilityRequired\": %b\n", isEligibilityRequired));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addSetAssetEligibilityAction(assetHash, isEligibilityRequired);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddChangeKycControllerAddressAction() {
        Wallet senderWallet = new Wallet();
        String accountHash = "FAccH1";
        String assetHash = "AssetH1";
        String kycControllerAddress = "KycCtrlAddr1";

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"ChangeKycControllerAddress\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"accountHash\": \"%s\",\n", accountHash));
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"kycControllerAddress\": \"%s\"\n", kycControllerAddress));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addChangeKycControllerAddressAction(accountHash, assetHash, kycControllerAddress);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }    

    @Test
    public void testAddAddKycProviderAction() {
        Wallet senderWallet = new Wallet();
        String assetHash = "AssetH1";
        String providerAddress = new Wallet().getAddress();

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"AddKycProvider\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"providerAddress\": \"%s\"\n", providerAddress));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addAddKycProviderAction(assetHash, providerAddress);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    @Test
    public void testAddRemoveKycProviderAction() {
        Wallet senderWallet = new Wallet();
        String assetHash = "AssetH1";
        String providerAddress = new Wallet().getAddress();

        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append(String.format("  \"senderAddress\": \"%s\",\n", senderWallet.getAddress()));
        sb.append("  \"nonce\": 1,\n");
        sb.append("  \"expirationTime\": 0,\n");
        sb.append("  \"actionFee\": 0.01,\n");        
        sb.append("  \"actions\": [\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"RemoveKycProvider\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"assetHash\": \"%s\",\n", assetHash));
        sb.append(String.format("        \"providerAddress\": \"%s\"\n", providerAddress));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addRemoveKycProviderAction(assetHash, providerAddress);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions: Multiple
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    public void testAddTransferChxActionMultiple() {
        Wallet senderWallet = new Wallet();
        Wallet recipientWallet1 = new Wallet();
        Wallet recipientWallet2 = new Wallet();
        float amount1 = 200;
        float amount2 = 300;

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
        sb.append(String.format("        \"recipientAddress\": \"%s\",\n", recipientWallet1.getAddress()));
        sb.append(String.format("        \"amount\": %4.1f\n", amount1));
        sb.append("      }\n");
        sb.append("    },\n");
        sb.append("    {\n");
        sb.append("      \"actionType\": \"TransferChx\",\n");
        sb.append("      \"actionData\": {\n");
        sb.append(String.format("        \"recipientAddress\": \"%s\",\n", recipientWallet2.getAddress()));
        sb.append(String.format("        \"amount\": %4.1f\n", amount2));
        sb.append("      }\n");
        sb.append("    }\n");
        sb.append("  ]\n");
        sb.append("}");
        String expected = sb.toString();
        Tx tx = new Tx(senderWallet.getAddress(), 1, 0.01f, 0);
        tx.addTransferChxAction(recipientWallet1.getAddress(), amount1);
        tx.addTransferChxAction(recipientWallet2.getAddress(), amount2);
        String actual = tx.toJson(true);
        assertEquals(expected, actual);  
    }
}
