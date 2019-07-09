import json
import crypto

class Tx:
    actions = []
    sender_address = ""
    nonce = 0
    
    def __init__(self, sender_address, nonce, action_fee=0, expiration_time=0):
        self.sender_address = sender_address
        self.nonce = nonce
        self.action_fee = action_fee
        self.expiration_time = expiration_time    
     
    @classmethod 
    def add_action(self, action_type, action_data):
        action = {'actionType': action_type, 'actionData': action_data}
        self.actions.append(action)
        
    @classmethod
    def add_transfer_chx_action(self, recipient_address, amount):
        self.add_action('TransferChx', {
            'recipientAddress' : recipient_address, 
            'amount': amount
        })
        
    @classmethod
    def add_delegate_stake_action(self, validator_address, amount):
        self.add_action('DelegateStake', {
            'validatorAddress' : validator_address, 
            'amount': amount
        })
    
    @classmethod
    def add_configure_validator_action(self, network_address, shared_reward_percent, is_enabled):
        self.add_action('ConfigureValidator', {
            'networkAddress' : network_address,
            'sharedRewardPercent' : shared_reward_percent, 
            'isEnabled': is_enabled
        })
    
    @classmethod
    def add_remove_validator_action(self, network_address, shared_reward_percent, is_enabled):
        self.add_action('RemoveValidator', {})
            
    @classmethod
    def add_transfer_asset_action(self, from_account_hash, to_account_hash, asset_hash, amount):
        self.add_action('TransferAsset', {
            'fromAccountHash' : from_account_hash,
            'toAccountHash' : to_account_hash, 
            'assetHash' : asset_hash, 
            'amount': amount
        })    
    
    @classmethod
    def add_create_asset_emission(self, emission_account_hash, asset_hash, amount):
        self.add_action('CreateAssetEmission', {
            'emissionAccountHash' : emission_account_hash, 
            'assetHash' : asset_hash, 
            'amount': amount
        })     
        
    @classmethod
    def add_create_asset_action(self):
        self.add_action('CreateAsset', {})
        return crypto.derive_hash(self.sender_address, self.nonce, len(self.actions))
        
    @classmethod
    def add_asset_code_action(self, asset_hash, asset_code):
        self.add_action('SetAssetCode', {
            'assetHash': asset_hash,
            'assetCode': asset_code
        })
        
    @classmethod
    def add_set_asset_controller_action(self, asset_hash, controller_address):
        self.add_action('SetAssetController', {
            'assetHash': asset_hash,
            'controllerAddress': controller_address
        })
            
    @classmethod
    def add_create_account_action(self):
        self.add_action('CreateAccount', {})
        return crypto.derive_hash(self.sender_address, self.nonce, len(self.actions))
    
    @classmethod
    def add_set_account_controller_action(self, account_hash, controller_address):
        self.add_action('SetAccountController', {
            'accountHash': account_hash,
            'controllerAddress': controller_address
        })
        
    @classmethod
    def add_submit_vote_action(self, account_hash, asset_hash, resolution_hash, vote_hash):
        self.add_action('SubmitVote', {
            'accountHash': account_hash,
            'assetHash': asset_hash,
            'resolutionHash': resolution_hash,
            'voteHash': vote_hash
        })
        
    @classmethod
    def add_submit_vote_weight_action(self, account_hash, asset_hash, resolution_hash, vote_weight):
        self.add_action('SubmitVoteWeight', {
            'accountHash': account_hash,
            'assetHash': asset_hash,
            'resolutionHash': resolution_hash,
            'voteWeight': vote_weight
        })                 
        
    @classmethod
    def add_set_account_eligibility_action(self, account_hash, asset_hash, is_primary_eligible, is_secondary_eligible):
        self.add_action('SetAccountEligibility', {
            'accountHash': account_hash,
            'assetHash': asset_hash,
            'isPrimaryEligible': is_primary_eligible,
            'isSecondaryEligible': is_secondary_eligible
        })
        
    @classmethod
    def add_set_asset_eligibility_action(self, asset_hash, is_eligibility_required):
        self.add_action('SetAssetEligibility', {
            'assetHash': asset_hash,
            'isEligibilityRequired': is_eligibility_required
        })
        
    @classmethod
    def add_change_kyc_controller_address(self, account_hash, asset_hash, kyc_controller_address):
        self.add_action('ChangeKycControllerAddress', {
            'accountHash': account_hash,
            'assetHash': asset_hash,
            'kycControllerAddress': kyc_controller_address
        })
    
    @classmethod
    def add_add_kyc_provider_address(self, asset_hash, provider_address):
        self.add_action('AddKycProvider', {
            'assetHash': asset_hash,
            'providerAddress': provider_address
        })
        
    @classmethod
    def add_remove_kyc_provider_address(self, asset_hash, provider_address):
        self.add_action('RemoveKycProvider', {
            'assetHash': asset_hash,
            'providerAddress': provider_address
        })                