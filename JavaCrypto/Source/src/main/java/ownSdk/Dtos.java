package ownSdk;

public class Dtos {

    public static class TxAction {

        public TxAction(String actionType, Object actionData) {
            setActionType(actionType);
            setActionData(actionData);
        }

        private String actionType;
        private Object actionData;
        
        public String getActionType() {
            return this.actionType;
        }

        private void setActionType(String actionType) {
            this.actionType = actionType;
        }

        public Object getActionData() {
            return this.actionData;
        }

        private void setActionData(Object actionData) {
            this.actionData = actionData;
        }
    }

    public static class SignedTx {

        public SignedTx(String tx, String signature) {
            setTx(tx);
            setSignature(signature);
        }

        private String tx;
        private String signature;     
        
        public String getTx() {
            return this.tx;
        }

        private void setTx(String tx){
            this.tx = tx;
        }

        public String getSignature() {
            return this.signature;
        }

        private void setSignature(String signature) {
            this.signature = signature;
        }
    }

    public static class TransferChxTxActionDto {

        public TransferChxTxActionDto(String recipientAddress, float amount){
            setRecipientAddress(recipientAddress);
            setAmount(amount);
        }

        private String recipientAddress;
        private float amount; 

        public String getRecipientAddress() {
            return this.recipientAddress;
        }

        private void setRecipientAddress(String recipientAddress){
            this.recipientAddress = recipientAddress;
        }

        public float getAmount() {
            return this.amount;
        }

        private void setAmount(float amount){
            this.amount = amount;
        }
    }

    public static class DelegateStakeTxActionDto {

        public DelegateStakeTxActionDto (String validatorAddress, float amount) {
            setValidatorAddress(validatorAddress);
            setAmount(amount);
        }

        private String validatorAddress;
        private float amount; 

        public String getValidatorAddress() {
            return this.validatorAddress;
        }

        private void setValidatorAddress(String validatorAddress) {
            this.validatorAddress = validatorAddress;
        }

        public float getAmount() {
            return this.amount;
        }

        private void setAmount(float amount){
            this.amount = amount;
        }
    }

    public static class ConfigureValidatorTxActionDto {

        public ConfigureValidatorTxActionDto (String networkAddress, float sharedRewardPercent, boolean isEnabled) {
            setNetworkAddress(networkAddress);
            setSharedRewardPercent(sharedRewardPercent);
            setIsEnabled(isEnabled);
        }

        private String networkAddress;
        private float sharedRewardPercent;
        private boolean isEnabled; 

        public String getNetworkAddress() {
            return this.networkAddress;
        }

        private void setNetworkAddress(String networkAddress) {
            this.networkAddress = networkAddress;
        }

        public float getSharedRewardPercent() {
            return this.sharedRewardPercent;
        }

        private void setSharedRewardPercent(float sharedRewardPercent) {
            this.sharedRewardPercent = sharedRewardPercent;
        }

        public boolean getIsEnabled() {
            return this.isEnabled;
        }

        private void setIsEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    public static class RemoveValidatorTxActionDto {}

    public static class TransferAssetTxActionDto {

        public TransferAssetTxActionDto (String fromAccountHash, String toAccountHash, String assetHash, float amount) {
            setFromAccountHash(fromAccountHash);
            setToAccountHash(toAccountHash);
            setAssetHash(assetHash);
            setAmount(amount);
        }        

        private String fromAccountHash;
        private String toAccountHash;
        private String assetHash;
        private float amount;

        public String getfromaccountHash() {
            return this.fromAccountHash;
        }
        
        private void setFromAccountHash(String fromAccountHash) {
            this.fromAccountHash = fromAccountHash;
        }                
        
        public String getToAccountHash() {
            return this.toAccountHash;
        }
        
        private void setToAccountHash(String toAccountHash) {
            this.toAccountHash = toAccountHash;
        }        
                
        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }                
        
        public float getAmount() {
            return this.amount;
        }
        
        private void setAmount(float amount) {
            this.amount = amount;
        }                    
    }

    public static class CreateAssetEmissionTxActionDto {

        public CreateAssetEmissionTxActionDto (String emissionAccountHash, String assetHash, float amount) { 
            setEmissionAccountHash(emissionAccountHash);
            setAssetHash(assetHash);
            setAmount(amount);
        }

        private String emissionAccountHash;
        private String assetHash;
        private float amount;

        public String getEmissionAccountHash() {
            return this.emissionAccountHash;
        }

        private void setEmissionAccountHash(String emissionAccountHash){
            this.emissionAccountHash = emissionAccountHash;
        }

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }                
        
        public float getAmount() {
            return this.amount;
        }
        
        private void setAmount(float amount) {
            this.amount = amount;
        }   
    }

    public static class CreateAssetTxActionDto {}

    public static class SetAssetCodeTxActionDto {

        public SetAssetCodeTxActionDto(String assetHash, String assetCode) {
            setAssetHash(assetHash);
            setAssetCode(assetCode);
        }
        private String assetHash;
        private String assetCode;        
        

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }    

        public String getAssetCode() {
            return this.assetCode;
        }

        private void setAssetCode(String assetCode) {
            this.assetCode = assetCode;
        }
    }

    public static class SetAssetControllerTxActionDto {

        public SetAssetControllerTxActionDto (String assetHash, String controllerAddress){
            setAssetHash(assetHash);
            setControllerAddress(controllerAddress);
        }

        private String assetHash;
        private String controllerAddress;     

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }   
        
        public String getControllerAddress(){ 
            return this.controllerAddress;
        }

        public void setControllerAddress(String controllerAddress){
            this.controllerAddress = controllerAddress;
        }        
    }

    public static class CreateAccountTxActionDto {}

    public static class SetAccountControllerTxActionDto {

        public SetAccountControllerTxActionDto(String accountHash, String controllerAddress) {
            setAccountHash(accountHash);
            setControllerAddress(controllerAddress);
        }
        private String accountHash;
        private String controllerAddress;

        public String getAccountHash() {
            return this.accountHash;
        }

        public void setAccountHash(String accountHash){
            this.accountHash = accountHash;
        }

        public String getControllerAddress() {
            return this.controllerAddress;
        }

        public void setControllerAddress(String controllerAddress) {
            this.controllerAddress = controllerAddress;
        }
    }

    public static class SubmitVoteTxActionDto {

        public SubmitVoteTxActionDto(String accountHash, String assetHash, String resolutionHash, String voteHash) {
            setAccountHash(accountHash);
            setAssetHash(assetHash);
            setResolutionHash(resolutionHash);
            setVoteHash(voteHash);
        }

        private String accountHash;
        private String assetHash;
        private String resolutionHash;
        private String voteHash;

        public String getAccountHash() {
            return this.accountHash;
        }

        public void setAccountHash(String accountHash){
            this.accountHash = accountHash;
        }

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }   

        public String getResolutionHash() {
            return this.resolutionHash;
        }

        private void setResolutionHash(String resolutionHash) {
            this.resolutionHash = resolutionHash;
        }

        public String getVoteHash() {
            return this.voteHash;
        }

        private void setVoteHash(String voteHash) {
            this.voteHash = voteHash;
        }
    }

    public static class SubmitVoteWeightTxActionDto {

        public SubmitVoteWeightTxActionDto(String accountHash, String assetHash, String resolutionHash, float voteWeight) {
            setAccountHash(accountHash);
            setAssetHash(assetHash);
            setResolutionHash(resolutionHash);
            setVoteWeight(voteWeight);
        }        

        private String accountHash;
        private String assetHash;
        private String resolutionHash;
        private float voteWeight;      
        
        public String getAccountHash() {
            return this.accountHash;
        }

        public void setAccountHash(String accountHash){
            this.accountHash = accountHash;
        }

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }  
        
        public String getResolutionHash() {
            return this.resolutionHash;
        }

        private void setResolutionHash(String resolutionHash) {
            this.resolutionHash = resolutionHash;
        }
        
        public float getVoteWeight() {
            return this.voteWeight;
        }

        public void setVoteWeight(float voteWeight){
            this.voteWeight = voteWeight;
        }
    }
    
    public static class SetAccountEligibilityTxActionDto {

        public SetAccountEligibilityTxActionDto(String accountHash, String assetHash, boolean isPrimaryEligible, boolean isSecondaryEligible) {
            setAccountHash(accountHash);
            setAssetHash(assetHash);
            setIsPrimaryEligible(isPrimaryEligible);
            setIsSecondaryEligible(isSecondaryEligible);
        }

        private String accountHash;
        private String assetHash;
        private boolean isPrimaryEligible;
        private boolean isSecondaryEligible;

        public String getAccountHash() {
            return this.accountHash;
        }

        public void setAccountHash(String accountHash){
            this.accountHash = accountHash;
        }

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }   

        public boolean getIsPrimaryEligible() {
            return this.isPrimaryEligible;
        }

        private void setIsPrimaryEligible(boolean isPrimaryEligible) {
            this.isPrimaryEligible = isPrimaryEligible;
        }

        public boolean getIsSecondaryEligible() {
            return this.isSecondaryEligible;
        }

        public void setIsSecondaryEligible(boolean isSecondaryEligible){
            this.isSecondaryEligible = isSecondaryEligible;
        }
    }

    public static class SetAssetEligibilityTxActionDto {

        public SetAssetEligibilityTxActionDto(String assetHash, boolean isEligibilityRequired) {
            setAssetHash(assetHash);
            setIsEligibilityRequired(isEligibilityRequired);
        }

        private String assetHash;
        private boolean isEligibilityRequired;   
        
        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }   

        public boolean getIsEligibilityRequired() {
            return this.isEligibilityRequired;
        }

        private void setIsEligibilityRequired(boolean isEligibilityRequired){
            this.isEligibilityRequired = isEligibilityRequired;
        }
    }

    public static class ChangeKycControllerAddressTxActionDto {

        public ChangeKycControllerAddressTxActionDto(String accountHash, String assetHash, String kycControllerAddress) {
            setAccountHash(accountHash);
            setAssetHash(assetHash);
            setKycControllerAddress(kycControllerAddress);
        }

        private String accountHash;
        private String assetHash;
        private String kycControllerAddress;

        public String getAccountHash() {
            return this.accountHash;
        }

        public void setAccountHash(String accountHash){
            this.accountHash = accountHash;
        }

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }   

        public String getKycControllerAddress() {
            return this.kycControllerAddress;
        }

        private void setKycControllerAddress(String kycControllerAddress) {
            this.kycControllerAddress = kycControllerAddress;
        }
    }

    public static class AddKycProviderTxActionDto {

        public AddKycProviderTxActionDto(String assetHash, String providerAddress) {
            setAssetHash(assetHash);
            setProviderAddress(providerAddress);
        }

        private String assetHash;
        private String providerAddress;

        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }   

        public String getProviderAddress() {
            return this.providerAddress;
        }

        private void setProviderAddress(String providerAddress){
            this.providerAddress = providerAddress;
        }
    }

    public static class RemoveKycProviderTxActionDto {

        public RemoveKycProviderTxActionDto(String assetHash, String providerAddress) {
            setAssetHash(assetHash);
            setProviderAddress(providerAddress);
        }        

        private String assetHash;
        private String providerAddress;       
        
        public String getAssetHash() {
            return this.assetHash;
        }
        
        private void setAssetHash(String assetHash) {
            this.assetHash = assetHash;
        }   
        
        public String getProviderAddress() {
            return this.providerAddress;
        }

        private void setProviderAddress(String providerAddress){
            this.providerAddress = providerAddress;
        }
    }
}