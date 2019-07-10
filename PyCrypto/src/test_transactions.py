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
