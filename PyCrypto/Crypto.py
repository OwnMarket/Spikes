import base58
import base64
import hashlib
import struct
from ecdsa import SigningKey, SECP256k1
import coincurve

####################################################################################################
## Encoding
####################################################################################################

def encode58(src):
    return base58.b58encode(src).decode()

def decode58(src):
    return base58.b58decode(src)

def encode64(src):
    return base64.b64encode(src).decode()

def decode64(src):
    return base64.b64decode(src)

####################################################################################################
## Hashing
####################################################################################################

def sha256(data): 
    return hashlib.sha256(data).digest()

def sha512(data):
    return hashlib.sha512(data).digest()

def sha160(data):
    return sha512(data)[:20]

def hash(data):
    return encode58(sha256(data))

def derive_hash(address, nonce, tx_action_number):
    address_bytes = decode58(address)
    nonce_bytes = nonce.to_bytes(8, byteorder='big')
    tx_action_number_bytes = tx_action_number.to_bytes(2, byteorder='big')
    return hash(address_bytes + nonce_bytes + tx_action_number_bytes)

def blockchain_address(public_key):
    prefix = bytes(bytearray.fromhex('065A'))
    public_key_hash_with_prefix = prefix + sha160(sha256(public_key))
    checksum = sha256(sha256(public_key_hash_with_prefix))[:4]
    return encode58(public_key_hash_with_prefix + checksum)

####################################################################################################
## Signing
####################################################################################################

def decompress(pk):
     return bytes(chr(4), 'ascii') + pk.to_string()
     
def generate_wallet(): 
    sk = SigningKey.generate(curve=SECP256k1)
    pk = sk.get_verifying_key()
    private_key = encode58(sk.to_string())
    address = blockchain_address(decompress(pk))
    return (private_key, address)    
    
def address_from_private_key(private_key):      
    private_key_bytes = decode58(private_key)
    sk = SigningKey.from_string(private_key_bytes, curve=SECP256k1)
    pk = sk.get_verifying_key()
    return blockchain_address(decompress(pk))

def wallet_from_private_key(private_key):
    address = address_from_private_key(private_key)
    return (private_key, address)

def sign(private_key, data_hash):
    private_key_bytes = decode58(private_key)
    sk = coincurve.PrivateKey(private_key_bytes)
    signature_bytes = sk.sign_recoverable(data_hash, None)        
    return encode58(signature_bytes)

def sign_message(network_code, private_key, message):
    message_hash = sha256(message)
    network_id_bytes = sha256(network_code.encode())
    data_hash = sha256(message_hash + network_id_bytes)
    return sign(private_key, data_hash)
    
def sign_plain_text(private_key, text): 
    data_hash = sha256(text)
    return sign(private_key, data_hash)
        
####################################################################################################
## Testing
####################################################################################################

def test_encode_decode_base64():
    original_data = 'Chainium'
    expected = 'Q2hhaW5pdW0='
    actual = encode64(original_data.encode())
    decoded = decode64(actual).decode()
    print('Expected = ', expected, ' | Actual = ', actual)
    print('Original = ', original_data, ' | Decoded = ', decoded)

def test_encode_decode_base58():
    original_data = 'Chainium'
    expected = 'CGwVR5Wyya4'
    actual = encode58(original_data.encode())
    decoded = decode58(actual).decode()
    print('Expected = ', expected, ' | Actual = ', actual)
    print('Original = ', original_data, ' | Decoded = ', decoded)

def test_hash(): 
    original_data = 'Chainium'
    expected = 'Dp6vNLdUbRTc1Y3i9uSBritNqvqe4es9MjjGrVi1nQMu'
    actual = hash(original_data.encode())
    print('Expected = ', expected, ' | Actual = ', actual)

def test_derive_hash():
    address = 'CHPJ6aVwpGBRf1dv6Ey1TuhJzt1VtCP5LYB'
    nonce = 32
    tx_action_number = 2
    expected = '5kHcMrwXUptjmbdR8XBW2yY3FkSFwnMdrVr22Yg39pTR'
    actual = derive_hash(address, nonce, tx_action_number)
    print('Expected = ', expected, ' | Actual = ', actual)

def test_generate_wallet():
    private_key, address = generate_wallet()
    expected = address   
    actual = address_from_private_key(private_key)
    print('Expected = ', expected, ' | Actual = ', actual)    
    
def test_address_from_private_key():
    private_key = '3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq'
    expected = 'CHGmdQdHfLPcMHtzyDzxAkTAQiRvKJrkYv8'
    actual = address_from_private_key(private_key)
    print('Expected = ', expected, ' | Actual = ', actual)    
    
def test_sign_message():
    networkCode = 'UNIT_TESTS'
    privateKey = '3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq'
    tx = 'Chainium'
    expected = 'EYzWMyZjqHkwsNFKcFEg4Q64m4jSUD7cAeKucyZ3a9MKeNmXTbRK3czqNVGj9RpkPGji9AtGiUxDtipqE3DtFPHxU'
    actual = sign_message(networkCode, privateKey, tx.encode())
    print('Expected = ', expected)    
    print('Actual =   ', actual)
    
def test_sign_plain_text():
    privateKey = '3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq'
    txt = 'Chainium'
    expected = 'EzCsWgPozyVT9o6TycYV6q1n4YK4QWixa6Lk4GFvwrj6RU3K1wHcwNPZJUMBYcsGp5oFhytHiThon5zqE8uLk8naB'
    actual = sign_plain_text(privateKey, txt.encode())
    print('Expected = ', expected)    
    print('Actual =   ', actual)
        
# test_encode_decode_base64()
# test_encode_decode_base58()
# test_hash()
# test_derive_hash()
# test_generate_wallet()
# test_address_from_private_key()
test_sign_message()
test_sign_plain_text()
