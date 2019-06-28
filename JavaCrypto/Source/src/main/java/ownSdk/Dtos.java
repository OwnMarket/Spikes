package ownSdk;

public class Dtos {

    public static class TxAction {
        public TxAction(String actionType, Object actionData) {
            ActionType = actionType;
            ActionData = actionData;
        }
        public String ActionType;
        public Object ActionData; 
    }

    public static class SignedTx {
        public SignedTx(String tx, String signature) {
            Tx = tx;
            Signature = signature;
        }
        public String Tx;
        public String Signature;        
    }

    public static class TransferChxTxActionDto {
        public TransferChxTxActionDto(String recipientAddress, float amount){
            RecipientAddress = recipientAddress;
            Amount = amount;
        }
        public String RecipientAddress;
        public float Amount; 
    }

    public static class DelegateStakeTxActionDto {
        public DelegateStakeTxActionDto (String validatorAddress, float amount) {
            ValidatorAddress = validatorAddress;
            Amount = amount;
        }
        public String ValidatorAddress;
        public float Amount; 
    }

    public static class ConfigureValidatorTxActionDto {
        public ConfigureValidatorTxActionDto (String networkAddress, float sharedRewardPercent, boolean isEnabled) {
            NetworkAddress = networkAddress;
            SharedRewardPercent = sharedRewardPercent;
            IsEnabled = isEnabled;
        }
        public String NetworkAddress;
        public float SharedRewardPercent;
        public boolean IsEnabled; 
    }

    public static class RemoveValidatorTxActionDto {}

    public static class TransferAssetTxActionDto {
        public TransferAssetTxActionDto (String fromAccountHash, String toAccountHash, String assetHash, float amount) {
            FromAccountHash = fromAccountHash;
            ToAccountHash = toAccountHash;
            AssetHash = assetHash;
            Amount = amount;
        }        
        public String FromAccountHash;
        public String ToAccountHash;
        public String AssetHash;
        public float Amount;
    }

    public static class CreateAssetEmissionTxActionDto {
        public CreateAssetEmissionTxActionDto (String emissionAccountHash, String assetHash, float amount) { 
            EmissionAccountHash = emissionAccountHash;
            AssetHash = assetHash;
            Amount = amount;
        }
        public String EmissionAccountHash;
        public String AssetHash;
        public float Amount;
    }

    public static class CreateAssetTxActionDto {}

    public static class SetAssetCodeTxActionDto {
        public SetAssetCodeTxActionDto(String assetHash, String assetCode) {
            AssetHash = assetHash;
            AssetCode = assetCode;
        }
        public String AssetHash;
        public String AssetCode;        
    }

    public static class SetAssetControllerTxActionDto {
        public SetAssetControllerTxActionDto (String assetHash, String controllerAddress){
            AssetHash = assetHash;
            ControllerAddress = controllerAddress;
        }
        public String AssetHash;
        public String ControllerAddress;        
    }

    public static class CreateAccountTxActionDto {}

    public static class SetAccountControllerTxActionDto {
        public SetAccountControllerTxActionDto(String accountHash, String controllerAddress) {
            AccountHash = accountHash;
            ControllerAddress = controllerAddress;
        }
        public String AccountHash;
        public String ControllerAddress;
    }

    public static class SubmitVoteTxActionDto {
        public SubmitVoteTxActionDto(String accountHash, String assetHash, String resolutionHash, String voteHash) {
            AccountHash = accountHash;
            AssetHash = assetHash;
            ResolutionHash = resolutionHash;
            VoteHash = voteHash;
        }
        public String AccountHash;
        public String AssetHash;
        public String ResolutionHash;
        public String VoteHash;
    }

    public static class SubmitVoteWeightTxActionDto {
        public SubmitVoteWeightTxActionDto(String accountHash, String assetHash, String resolutionHash, float voteWeight) {
            AccountHash = accountHash;
            AssetHash = assetHash;
            ResolutionHash = resolutionHash;
            VoteWeight = voteWeight;
        }        
        public String AccountHash;
        public String AssetHash;
        public String ResolutionHash;
        public float VoteWeight;        
    }
    
    public static class SetAccountEligibilityTxActionDto {
        public SetAccountEligibilityTxActionDto(String accountHash, String assetHash, boolean isPrimaryEligible, boolean isSecondaryEligible) {
            AccountHash = accountHash;
            AssetHash = assetHash;
            IsPrimaryEligible = isPrimaryEligible;
            IsSecondaryEligible = isSecondaryEligible;
        }
        public String AccountHash;
        public String AssetHash;
        public boolean IsPrimaryEligible;
        public boolean IsSecondaryEligible;
    }

    public static class SetAssetEligibilityTxActionDto {
        public SetAssetEligibilityTxActionDto(String assetHash, boolean isEligibilityRequired) {
            AssetHash = assetHash;
            IsEligibilityRequired = isEligibilityRequired;
        }
        public String AssetHash;
        public boolean IsEligibilityRequired;        
    }

    public static class ChangeKycControllerAddressTxActionDto {
        public ChangeKycControllerAddressTxActionDto(String accountHash, String assetHash, String kycControllerAddress) {
            AccountHash = accountHash;
            AssetHash = assetHash;
            KycControllerAddress = kycControllerAddress;
        }
        public String AccountHash;
        public String AssetHash;
        public String KycControllerAddress;
    }

    public static class AddKycProviderTxActionDto {
        public AddKycProviderTxActionDto(String assetHash, String providerAddress) {
            AssetHash = assetHash;
            ProviderAddress = providerAddress;
        }
        public String AssetHash;
        public String ProviderAddress;
    }

    public static class RemoveKycProviderTxActionDto {
        public RemoveKycProviderTxActionDto(String assetHash, String providerAddress) {
            AssetHash = assetHash;
            ProviderAddress = providerAddress;
        }        
        public String AssetHash;
        public String ProviderAddress;        
    }
}