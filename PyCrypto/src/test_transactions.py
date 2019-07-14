import pytest
import crypto
import transactions

####################################################################################################
## Tx
####################################################################################################
    
def test_create_tx_return_correct_json():
    sender_wallet = crypto.Wallet()
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 123,
    "actionFee": 0.01,
    "actions": []
}""" % (sender_wallet.address)    
    actual = transactions.Tx(sender_wallet.address, 1, 0.01, 123).to_json()
    assert expected == actual
    
def test_create_tx_expiration_time_zero_if_not_provided():
    sender_wallet = crypto.Wallet()
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": []
}""" % (sender_wallet.address)    
    actual = transactions.Tx(sender_wallet.address, 1, 0.01, 0).to_json()
    assert expected == actual    

####################################################################################################
## Actions: Network Management
####################################################################################################
    
def test_add_transfer_chx_action():
    sender_wallet = crypto.Wallet()
    recipient_wallet = crypto.Wallet()
    amount = 1000
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "TransferChx",
            "actionData": {
                "recipientAddress": "%s",
                "amount": %d
            }
        }
    ]
}""" % (sender_wallet.address, recipient_wallet.address, amount)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_transfer_chx_action(recipient_wallet.address, amount)
    actual = tx.to_json()
    assert expected == actual   
    
def test_add_delegate_stake_action():
    sender_wallet = crypto.Wallet()
    validator_wallet = crypto.Wallet()
    amount = 100000
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "DelegateStake",
            "actionData": {
                "validatorAddress": "%s",
                "amount": %d
            }
        }
    ]
}""" % (sender_wallet.address, validator_wallet.address, amount)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_delegate_stake_action(validator_wallet.address, amount)
    actual = tx.to_json()
    assert expected == actual       
    
def test_configure_validator_action():
    sender_wallet = crypto.Wallet()
    network_address = 'val01.some.domain.com:25718'
    shared_reward_percent = 42
    is_enabled = True
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "ConfigureValidator",
            "actionData": {
                "networkAddress": "%s",
                "sharedRewardPercent": %d,
                "isEnabled": true
            }
        }
    ]
}""" % (sender_wallet.address, network_address, shared_reward_percent)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_configure_validator_action(network_address, shared_reward_percent, is_enabled)
    actual = tx.to_json()
    assert expected == actual       
    
def test_remove_validator_action():
    sender_wallet = crypto.Wallet()
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "RemoveValidator",
            "actionData": {}
        }
    ]
}""" % (sender_wallet.address)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_remove_validator_action()
    actual = tx.to_json()
    assert expected == actual    

####################################################################################################
## Actions: Asset Management
####################################################################################################

def test_add_transfer_asset_action():
    sender_wallet = crypto.Wallet()
    from_account_hash = 'FAccH1'
    to_account_hash = 'TAccH1'
    asset_hash = 'AssetH1'
    amount = 100
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "TransferAsset",
            "actionData": {
                "fromAccountHash": "%s",
                "toAccountHash": "%s",
                "assetHash": "%s",
                "amount": %d
            }
        }
    ]
}""" % (sender_wallet.address, from_account_hash, to_account_hash, asset_hash, amount)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_transfer_asset_action(from_account_hash, to_account_hash, asset_hash, amount)
    actual = tx.to_json()
    assert expected == actual
    
def test_create_asset_emission_action():
    sender_wallet = crypto.Wallet()
    emission_account_hash = 'EAccH1'
    asset_hash = 'AssetH1'
    amount = 100
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "CreateAssetEmission",
            "actionData": {
                "emissionAccountHash": "%s",
                "assetHash": "%s",
                "amount": %d
            }
        }
    ]
}""" % (sender_wallet.address, emission_account_hash, asset_hash, amount)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_create_asset_emission_action(emission_account_hash, asset_hash, amount)
    actual = tx.to_json()
    assert expected == actual
    
def test_create_asset_action():
    sender_wallet = crypto.Wallet()
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "CreateAsset",
            "actionData": {}
        }
    ]
}""" % (sender_wallet.address)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_create_asset_action()
    actual = tx.to_json()
    assert expected == actual  
    
def test_create_asset_action_returns_asset_hash():
    sender_wallet = crypto.Wallet()
    nonce = 1
    expected = crypto.derive_hash(sender_wallet.address, nonce, 1)    
    
    tx = transactions.Tx(sender_wallet.address, nonce, 0.01, 0)
    actual = tx.add_create_asset_action()    
    
    assert expected == actual   

def test_set_asset_code_action():
    sender_wallet = crypto.Wallet()
    asset_hash = 'AssetH1'
    asset_code = 'AST1'
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "SetAssetCode",
            "actionData": {
                "assetHash": "%s",
                "assetCode": "%s"
            }
        }
    ]
}""" % (sender_wallet.address, asset_hash, asset_code)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_set_asset_code_action(asset_hash, asset_code)
    actual = tx.to_json()
    assert expected == actual
    
def test_set_asset_controller_action():
    sender_wallet = crypto.Wallet()
    controllerWallet = crypto.Wallet()
    asset_hash = 'AssetH1'
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "SetAssetController",
            "actionData": {
                "assetHash": "%s",
                "controllerAddress": "%s"
            }
        }
    ]
}""" % (sender_wallet.address, asset_hash, controllerWallet.address)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_set_asset_controller_action(asset_hash, controllerWallet.address)
    actual = tx.to_json()
    assert expected == actual
    
def test_create_account_action():
    sender_wallet = crypto.Wallet()
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "CreateAccount",
            "actionData": {}
        }
    ]
}""" % (sender_wallet.address)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_create_account_action()
    actual = tx.to_json()
    assert expected == actual  
    
def test_create_account_action_returns_account_hash():
    sender_wallet = crypto.Wallet()
    nonce = 1
    expected = crypto.derive_hash(sender_wallet.address, nonce, 1)    
    
    tx = transactions.Tx(sender_wallet.address, nonce, 0.01, 0)
    actual = tx.add_create_account_action()    
    
    assert expected == actual   

def test_set_account_controller_action():
    sender_wallet = crypto.Wallet()
    controllerWallet = crypto.Wallet()
    account_hash = 'AccountH1'
    
    expected = """{
    "senderAddress": "%s",
    "nonce": 1,
    "expirationTime": 0,
    "actionFee": 0.01,
    "actions": [
        {
            "actionType": "SetAccountController",
            "actionData": {
                "accountHash": "%s",
                "controllerAddress": "%s"
            }
        }
    ]
}""" % (sender_wallet.address, account_hash, controllerWallet.address)    
    tx = transactions.Tx(sender_wallet.address, 1, 0.01, 0)
    tx.add_set_account_controller_action(account_hash, controllerWallet.address)
    actual = tx.to_json()
    assert expected == actual
    
