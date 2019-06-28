package ownSdk;

import java.util.ArrayList;

public class Tx {

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    public Tx(String senderAddress, long nonce) {
        this(senderAddress, nonce, 0, 0);
    }

    public Tx(String senderAddress, long nonce, long expirationTime, float actionFee) {
        SenderAddress = senderAddress;
        Nonce = nonce;
        ExpirationTime = expirationTime;
        ActionFee = actionFee;
        Actions = new ArrayList<Dtos.TxAction>();
    }

    public String SenderAddress ;
    public long Nonce;
    public long ExpirationTime;
    public float ActionFee;
    public ArrayList<Dtos.TxAction> Actions;

    ////////////////////////////////////////////////////////////////////////////////////////////////////
    // Actions
    ////////////////////////////////////////////////////////////////////////////////////////////////////

    private void addAction(String actionType, Object actionData) {
        Dtos.TxAction txAction = new Dtos.TxAction(actionType, actionData);
        Actions.add(txAction);
    }

    public void AddTransferChxAction(String recipientAddress, float amount) {
        Dtos.TransferChxTxActionDto dto = new Dtos.TransferChxTxActionDto(recipientAddress, amount);
        addAction("TransferChx", dto);
    }

    public void AddDelegateStakeAction(String validatorAddress, float amount)  {
        Dtos.DelegateStakeTxActionDto dto = new Dtos.DelegateStakeTxActionDto(validatorAddress, amount);
        addAction("DelegateStake", dto);
    }

    public void AddConfigureValidatorAction(String networkAddress, float sharedRewardPercent, boolean isEnabled) {
        Dtos.ConfigureValidatorTxActionDto dto = new Dtos.ConfigureValidatorTxActionDto(networkAddress, sharedRewardPercent, isEnabled);
        addAction("ConfigureValidator", dto);
    }

    public void AddRemoveValidatorAction() {
        Dtos.RemoveValidatorTxActionDto dto = new Dtos.RemoveValidatorTxActionDto();
        addAction("RemoveValidator", dto);
    }

    public void AddTransferAssetAction(String fromAccountHash, String toAccountHash, String assetHash, float amount) {
        Dtos.TransferAssetTxActionDto dto = new Dtos.TransferAssetTxActionDto(fromAccountHash, toAccountHash, assetHash, amount);
        addAction("TransferAsset", dto);
    }

    public void AddCreateAssetEmissionAction(String emissionAccountHash, String assetHash, float amount) {
        Dtos.CreateAssetEmissionTxActionDto dto = new Dtos.CreateAssetEmissionTxActionDto(emissionAccountHash, assetHash, amount);
        addAction("CreateAssetEmission", dto);
    }

    public void AddCreateAssetAction() {
        Dtos.CreateAssetTxActionDto dto = new Dtos.CreateAssetTxActionDto();
        addAction("CreateAsset", dto);
    }

    public void AddSetAssetCodeAction(String assetHash, String assetCode) {
        Dtos.SetAssetCodeTxActionDto dto = new Dtos.SetAssetCodeTxActionDto(assetHash, assetCode);
        addAction("SetAssetCode", dto);
    }

    public void AddSetAssetControllerAction(String assetHash, String controllerAddress) {
        Dtos.SetAssetControllerTxActionDto dto = new Dtos.SetAssetControllerTxActionDto(assetHash, controllerAddress);
        addAction("SetAssetController", dto);
    }

    public void AddCreateAccountAction() {
        Dtos.CreateAccountTxActionDto dto = new Dtos.CreateAccountTxActionDto();
        addAction("CreateAccount", dto);
    }

    public void AddSetAccountControllerAction(String accountHash, String controllerAddress) {
        Dtos.SetAccountControllerTxActionDto dto = new Dtos.SetAccountControllerTxActionDto(accountHash, controllerAddress);
        addAction("SetAccountController", dto);
    }

    public void AddSubmitVoteAction(String accountHash, String assetHash, String resolutionHash, String voteHash) {
        Dtos.SubmitVoteTxActionDto dto = new Dtos.SubmitVoteTxActionDto(accountHash, assetHash, resolutionHash, voteHash);
        addAction("SubmitVote", dto);
    }

    public void AddSubmitVoteWeightAction(String accountHash, String assetHash, String resolutionHash, float voteWeight) {
        Dtos.SubmitVoteWeightTxActionDto dto = new Dtos.SubmitVoteWeightTxActionDto(accountHash, assetHash, resolutionHash, voteWeight);
        addAction("SubmitVoteWeight", dto);
    }

    public void AddSetAccountEligibilityAction(String accountHash, String assetHash, boolean isPrimaryEligible, boolean isSecondaryEligible) {
        Dtos.SetAccountEligibilityTxActionDto dto = new Dtos.SetAccountEligibilityTxActionDto(accountHash, assetHash, isPrimaryEligible, isSecondaryEligible);
        addAction("SetAccountEligibility", dto);
    }

    public void AddSetAssetEligibilityAction(String assetHash, boolean isEligibilityRequired) {
        Dtos.SetAssetEligibilityTxActionDto dto = new Dtos.SetAssetEligibilityTxActionDto(assetHash, isEligibilityRequired);
        addAction("SetAssetEligibility", dto);
    }

    public void AddChangeKycControllerAddressAction(String accountHash, String assetHash, String kycControllerAddress) {
        Dtos.ChangeKycControllerAddressTxActionDto dto = new Dtos.ChangeKycControllerAddressTxActionDto(accountHash, assetHash, kycControllerAddress);
        addAction("ChangeKycControllerAddress", dto);
    }

    public void AddAddKycProviderAction(String assetHash, String providerAddress) {
        Dtos.AddKycProviderTxActionDto dto = new Dtos.AddKycProviderTxActionDto(assetHash, providerAddress);
        addAction("AddKycProvider", dto);
    }

    public void AddRemoveKycProviderAction(String assetHash, String providerAddress) {
        Dtos.RemoveKycProviderTxActionDto dto = new Dtos.RemoveKycProviderTxActionDto(assetHash, providerAddress);
        addAction("RemoveKycProvider", dto);
    }
}