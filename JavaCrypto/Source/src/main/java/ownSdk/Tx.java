package ownSdk;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Tx {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public Tx(String senderAddress, long nonce) {
        this(senderAddress, nonce, 0, 0);
    }

    public Tx(String senderAddress, long nonce, float actionFee, long expirationTime) {
        this.senderAddress = senderAddress;
        this.nonce = nonce;
        this.expirationTime = expirationTime;
        this.actionFee = actionFee;
        this.actions = new ArrayList<Dtos.TxAction>();
    }

    private String senderAddress;
    private long nonce;
    private long expirationTime;
    private float actionFee;
    private ArrayList<Dtos.TxAction> actions;

    public String getAddress() {
        return this.senderAddress;
    }

    public void setAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public long getNonce() {
        return this.nonce;
    }

    public void setNonce(long nonce) {
        this.nonce = nonce;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public float getActionFee() {
        return this.actionFee;
    }

    public void setActionFee(float actionFee) {
        this.actionFee = actionFee;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addAction(String actionType, Object actionData) {
        Dtos.TxAction txAction = new Dtos.TxAction(actionType, actionData);
        this.actions.add(txAction);
    }

    public void addTransferChxAction(String recipientAddress, float amount) {
        Dtos.TransferChxTxActionDto dto = new Dtos.TransferChxTxActionDto(recipientAddress, amount);
        addAction("TransferChx", dto);
    }

    public void addDelegateStakeAction(String validatorAddress, float amount) {
        Dtos.DelegateStakeTxActionDto dto = new Dtos.DelegateStakeTxActionDto(validatorAddress, amount);
        addAction("DelegateStake", dto);
    }

    public void addConfigureValidatorAction(String networkAddress, float sharedRewardPercent, boolean isEnabled) {
        Dtos.ConfigureValidatorTxActionDto dto = new Dtos.ConfigureValidatorTxActionDto(networkAddress,
                sharedRewardPercent, isEnabled);
        addAction("ConfigureValidator", dto);
    }

    public void addRemoveValidatorAction() {
        Dtos.RemoveValidatorTxActionDto dto = new Dtos.RemoveValidatorTxActionDto();
        addAction("RemoveValidator", dto);
    }

    public void addTransferAssetAction(String fromAccountHash, String toAccountHash, String assetHash, float amount) {
        Dtos.TransferAssetTxActionDto dto = new Dtos.TransferAssetTxActionDto(fromAccountHash, toAccountHash, assetHash,
                amount);
        addAction("TransferAsset", dto);
    }

    public void addCreateAssetEmissionAction(String emissionAccountHash, String assetHash, float amount) {
        Dtos.CreateAssetEmissionTxActionDto dto = new Dtos.CreateAssetEmissionTxActionDto(emissionAccountHash,
                assetHash, amount);
        addAction("CreateAssetEmission", dto);
    }

    public String addCreateAssetAction() {
        Dtos.CreateAssetTxActionDto dto = new Dtos.CreateAssetTxActionDto();
        addAction("CreateAsset", dto);
        return Crypto.deriveHash(this.senderAddress, this.nonce, (short) this.actions.size());
    }

    public void addSetAssetCodeAction(String assetHash, String assetCode) {
        Dtos.SetAssetCodeTxActionDto dto = new Dtos.SetAssetCodeTxActionDto(assetHash, assetCode);
        addAction("SetAssetCode", dto);
    }

    public void addSetAssetControllerAction(String assetHash, String controllerAddress) {
        Dtos.SetAssetControllerTxActionDto dto = new Dtos.SetAssetControllerTxActionDto(assetHash, controllerAddress);
        addAction("SetAssetController", dto);
    }

    public String addCreateAccountAction() {
        Dtos.CreateAccountTxActionDto dto = new Dtos.CreateAccountTxActionDto();
        addAction("CreateAccount", dto);
        return Crypto.deriveHash(this.senderAddress, this.nonce, (short) this.actions.size());
    }

    public void addSetAccountControllerAction(String accountHash, String controllerAddress) {
        Dtos.SetAccountControllerTxActionDto dto = new Dtos.SetAccountControllerTxActionDto(accountHash,
                controllerAddress);
        addAction("SetAccountController", dto);
    }

    public void addSubmitVoteAction(String accountHash, String assetHash, String resolutionHash, String voteHash) {
        Dtos.SubmitVoteTxActionDto dto = new Dtos.SubmitVoteTxActionDto(accountHash, assetHash, resolutionHash,
                voteHash);
        addAction("SubmitVote", dto);
    }

    public void addSubmitVoteWeightAction(String accountHash, String assetHash, String resolutionHash,
            float voteWeight) {
        Dtos.SubmitVoteWeightTxActionDto dto = new Dtos.SubmitVoteWeightTxActionDto(accountHash, assetHash,
                resolutionHash, voteWeight);
        addAction("SubmitVoteWeight", dto);
    }

    public void addSetAccountEligibilityAction(String accountHash, String assetHash, boolean isPrimaryEligible,
            boolean isSecondaryEligible) {
        Dtos.SetAccountEligibilityTxActionDto dto = new Dtos.SetAccountEligibilityTxActionDto(accountHash, assetHash,
                isPrimaryEligible, isSecondaryEligible);
        addAction("SetAccountEligibility", dto);
    }

    public void addSetAssetEligibilityAction(String assetHash, boolean isEligibilityRequired) {
        Dtos.SetAssetEligibilityTxActionDto dto = new Dtos.SetAssetEligibilityTxActionDto(assetHash,
                isEligibilityRequired);
        addAction("SetAssetEligibility", dto);
    }

    public void addChangeKycControllerAddressAction(String accountHash, String assetHash, String kycControllerAddress) {
        Dtos.ChangeKycControllerAddressTxActionDto dto = new Dtos.ChangeKycControllerAddressTxActionDto(accountHash,
                assetHash, kycControllerAddress);
        addAction("ChangeKycControllerAddress", dto);
    }

    public void addAddKycProviderAction(String assetHash, String providerAddress) {
        Dtos.AddKycProviderTxActionDto dto = new Dtos.AddKycProviderTxActionDto(assetHash, providerAddress);
        addAction("AddKycProvider", dto);
    }

    public void addRemoveKycProviderAction(String assetHash, String providerAddress) {
        Dtos.RemoveKycProviderTxActionDto dto = new Dtos.RemoveKycProviderTxActionDto(assetHash, providerAddress);
        addAction("RemoveKycProvider", dto);
    }

    public String toJson(boolean indentation) {        
        Gson gson = 
            indentation 
            ? new GsonBuilder().setPrettyPrinting().create()
            : new Gson();
        return gson.toJson(this);
    }

    public Dtos.SignedTx sign(String networkCode, String privateKey) {
        String json = toJson(false);
        String signature = Crypto.signMessage(networkCode, privateKey, json);
        return new Dtos.SignedTx(Crypto.encode64(json.getBytes()), signature);
    }    
}

