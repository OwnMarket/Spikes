import base58
import base64
import hashlib
import struct

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

def deriveHash(address, nonce, txActionNumber):
    addressB = decode58(address)
    nonceB = (nonce).to_bytes(8, byteorder='big')
    txActionNumberB = (txActionNumber).to_bytes(2, byteorder='big')
    return hash(addressB + nonceB + txActionNumberB)

def blockchainAddress(publicKey):
    prefix = bytearray.fromhex('065A')
    publicKeyHashWithPrefix = prefix + sha160(sha256(publicKey))
    checksum = sha256(sha256(publicKeyHashWithPrefix))[:4]
    return encode58(publicKeyHashWithPrefix + checksum)

####################################################################################################
## Testing
####################################################################################################

def testEncodeDecodeBase64():
    originalData = 'Chainium'
    expected = 'Q2hhaW5pdW0='
    actual = encode64(originalData.encode())
    decoded = decode64(actual).decode()
    print('Expected = ', expected, ' | Actual = ', actual)
    print('Original = ', originalData, ' | Decoded = ', decoded)

def testEncodeDecodeBase58():
    originalData = 'Chainium'
    expected = 'CGwVR5Wyya4'
    actual = encode58(originalData.encode())
    decoded = decode58(actual).decode()
    print('Expected = ', expected, ' | Actual = ', actual)
    print('Original = ', originalData, ' | Decoded = ', decoded)

def testHash(): 
    originalData = 'Chainium'
    expected = 'Dp6vNLdUbRTc1Y3i9uSBritNqvqe4es9MjjGrVi1nQMu'
    actual = hash(originalData.encode())
    print('Expected = ', expected, ' | Actual = ', actual)

def testDeriveHash():
    address = 'CHPJ6aVwpGBRf1dv6Ey1TuhJzt1VtCP5LYB'
    nonce = 32
    txActionNumber = 2
    expected = '5kHcMrwXUptjmbdR8XBW2yY3FkSFwnMdrVr22Yg39pTR'
    actual = deriveHash(address, nonce, txActionNumber)
    print('Expected = ', expected, ' | Actual = ', actual)

testEncodeDecodeBase64()
testEncodeDecodeBase58()
testHash()
testDeriveHash()
