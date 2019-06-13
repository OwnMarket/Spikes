package main

import (
	"crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"
	"crypto/sha256"
	"crypto/sha512"
	"fmt"

	"github.com/ethereum/go-ethereum/crypto"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
	"github.com/mr-tron/base58"
)

////////////////////////////////////////////////////////////////////////////////////////////////////
// Encoding
////////////////////////////////////////////////////////////////////////////////////////////////////

func encode58(src []byte) string {
	return base58.Encode(src)
}

func decode58(src string) []byte {
	decoded, err := base58.Decode(src)
	if err != nil {
		fmt.Println(err.Error())
	}
	return decoded
}

////////////////////////////////////////////////////////////////////////////////////////////////////
// Hashing
////////////////////////////////////////////////////////////////////////////////////////////////////

func xsha256(data []byte) [32]byte {
	return sha256.Sum256(data)
}

func xsha512(data []byte) [64]byte {
	return sha512.Sum512(data)
}

func xsha160(data []byte) []byte {
	_sha512 := xsha512(data)
	return _sha512[:20]
}

func hash(data []byte) string {
	_sha256 := xsha256(data)
	return encode58(_sha256[:])
}

func blockchainAddress(publicKey []byte) string {
	addressPrefix := []byte{6, 90} //CH
	_xsha256 := xsha256(publicKey)
	_xsha160_256 := xsha160(_xsha256[:])
	publicKeyHashWithPrefix := append(addressPrefix, _xsha160_256...)
	_xsha256 = xsha256(publicKeyHashWithPrefix)
	_xsha256_256 := xsha256(_xsha256[:])
	checksum := _xsha256_256[:4]
	return encode58(append(publicKeyHashWithPrefix, checksum...))
}

////////////////////////////////////////////////////////////////////////////////////////////////////
// Signing
////////////////////////////////////////////////////////////////////////////////////////////////////

func generateWallet() (privateKey string, address string) {
	key, err := ecdsa.GenerateKey(secp256k1.S256(), rand.Reader)
	if err != nil {
		panic(err)
	}

	publicKey := elliptic.Marshal(secp256k1.S256(), key.X, key.Y)

	privateKeyBytes := make([]byte, 32)
	blob := key.D.Bytes()
	copy(privateKeyBytes[32-len(blob):], blob)

	return encode58(privateKeyBytes), blockchainAddress(publicKey)
}

func addressFromPrivateKey(privateKey string) string {
	bytes := decode58(privateKey)
	key, err := crypto.ToECDSA(bytes)
	if err != nil {
		fmt.Println(err.Error())
		return ""
	}

	publicKey := elliptic.Marshal(secp256k1.S256(), key.X, key.Y)
	return blockchainAddress(publicKey)
}

func signMessage(networkCode []byte, privateKey string, message []byte) string {
	messageHash := xsha256(message)
	networkIdBytes := xsha256(networkCode)
	dataToSign := xsha256(append(messageHash[:], networkIdBytes[:]...))
	privateKeyBytes := decode58(privateKey)
	signatureBytes, err := secp256k1.Sign(dataToSign[:], privateKeyBytes)
	if err != nil {
		fmt.Println(err.Error())
		return ""
	}
	return encode58(signatureBytes)
}

func main() {
	// Encoding example
	encoded := encode58([]byte("Chainium"))
	decoded := decode58(encoded)
	fmt.Println("Expected = Chainium\nDecoded =", string(decoded))
	fmt.Println()

	// Wallet example
	privateKey, address := generateWallet()
	_addressFromPrivateKey := addressFromPrivateKey(privateKey)
	fmt.Println("Expected CHX Address = ", address)
	fmt.Println("Computed CHX Address = ", _addressFromPrivateKey)
	fmt.Println()

	// Signing example
	msg := []byte("Chainium")
	networkCode := []byte("UNIT_TESTS") //TODO: replace with OWN_PUBLIC_BLOCKCHAIN_MAINNET for mainnet!
	sig := signMessage(networkCode, "B6WNNx9oK8qRUU52PpzjXHZuv4NUb3Z33hdju3hhrceS", msg)
	fmt.Println("Expected Signature = ", "6Hhxz2eP3AagR56mP4AAaKViUxHi3gM9c5weLDR48x4X4ynRBDfxsHGjhX9cni1mtCkNxbnZ783YPgMwVYV52X1w5")
	fmt.Println("Computed Signature = ", sig)
}
