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
