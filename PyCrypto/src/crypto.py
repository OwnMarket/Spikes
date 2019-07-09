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
    message_hash = sha256(message.encode())
    network_id_bytes = sha256(network_code.encode())
    data_hash = sha256(message_hash + network_id_bytes)
    return sign(private_key, data_hash)
    
def sign_plain_text(private_key, text): 
    data_hash = sha256(text.encode())
    return sign(private_key, data_hash)

def verify_plain_text_signature(signature, text):
    data_hash = sha256(text.encode())
    signature_bytes = decode58(signature)
    pk = coincurve.PublicKey.from_signature_and_message(signature_bytes, data_hash, None)
    return blockchain_address(pk.format(compressed=False))

####################################################################################################
## Wallet
####################################################################################################

class Wallet:
    private_key = None
    address = None
    
    def __init__(self, private_key=None, address=None):
        if address == None:
            if private_key == None:
                self.private_key, self.address = generate_wallet()
            else:
                self.private_key = private_key
                self.address = address_from_private_key(private_key)
        else:
            self.private_key = private_key
            self.address = address
                        