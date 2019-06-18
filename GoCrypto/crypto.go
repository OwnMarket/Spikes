package main

import (
	"crypto/aes"
	"crypto/cipher"
	"crypto/ecdsa"
	"crypto/elliptic"
	"crypto/rand"
	"crypto/sha256"
	"crypto/sha512"
	"fmt"
	"io"

	"github.com/ethereum/go-ethereum/crypto"
	"github.com/ethereum/go-ethereum/crypto/secp256k1"
	"github.com/mr-tron/base58"
)

////////////////////////////////////////////////////////////////////////////////////////////////////
// Encryption
////////////////////////////////////////////////////////////////////////////////////////////////////

func encrypt(text []byte, passwordHash [32]byte) []byte {
	cypher, err := aes.NewCipher(passwordHash[:])
	if err != nil {
		fmt.Println(err)
		return make([]byte, 0)
	}

	gcm, err := cipher.NewGCM(cypher)
	if err != nil {
		fmt.Println(err)
		return make([]byte, 0)
	}

	nonce := make([]byte, gcm.NonceSize())
	if _, err := io.ReadFull(rand.Reader, nonce); err != nil {
		panic(err.Error())
	}

	return gcm.Seal(nonce, nonce, text, nil)
}

func decrypt(encryptedText []byte, passwordHash [32]byte) []byte {
	cypher, err := aes.NewCipher(passwordHash[:])
	if err != nil {
		fmt.Println(err)
	}

	gcm, err := cipher.NewGCM(cypher)
	if err != nil {
		fmt.Println(err)
	}

	nonceSize := gcm.NonceSize()
	if len(encryptedText) < nonceSize {
		fmt.Println(err)
	}
	nonce, encryptedText := encryptedText[:nonceSize], encryptedText[nonceSize:]
	text, err := gcm.Open(nil, nonce, encryptedText, nil)
	if err != nil {
		panic(err.Error())
	}

	return text
}

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

func sign(privateKey string, dataHash [32]byte) string {
	privateKeyBytes := decode58(privateKey)
	signatureBytes, err := secp256k1.Sign(dataHash[:], privateKeyBytes)
	if err != nil {
		fmt.Println(err.Error())
		return ""
	}
	return encode58(signatureBytes)
}

func signMessage(networkCode []byte, privateKey string, message []byte) string {
	messageHash := xsha256(message)
	networkIdBytes := xsha256(networkCode)
	dataToSign := xsha256(append(messageHash[:], networkIdBytes[:]...))
	return sign(privateKey, dataToSign)
}

func signPlainText(privateKey string, text []byte) string {
	dataToSign := xsha256(text)
	return sign(privateKey, dataToSign)
}

func verifyPlainTextSignature(signature string, text []byte) string {
	dataToVerify := xsha256(text)
	signatureBytes := decode58(signature)
	publicKey, err := secp256k1.RecoverPubkey(dataToVerify[:], signatureBytes)
	if err != nil {
		fmt.Println(err.Error())
		return ""
	}
	return blockchainAddress(publicKey)
}

func main() {
	// Encryption example
	password := []byte("pass")
	passwordHash := xsha256(password)
	encryptedText := encrypt([]byte("Chainium"), passwordHash)
	decryptedText := decrypt(encryptedText, passwordHash)
	fmt.Println("ENCRYPTION")
	fmt.Println("===========================================")
	fmt.Println("Expected = Chainium\nDecrypted = ", string(decryptedText))
	fmt.Println()

	// Encoding example
	encoded := encode58([]byte("Chainium"))
	decoded := decode58(encoded)
	fmt.Println("ENCODING")
	fmt.Println("===========================================")
	fmt.Println("Expected = Chainium\nDecoded =", string(decoded))
	fmt.Println()

	// Wallet example
	privateKey, address := generateWallet()
	_addressFromPrivateKey := addressFromPrivateKey(privateKey)
	fmt.Println("WALLET")
	fmt.Println("===========================================")
	fmt.Println("Expected CHX Address = ", address)
	fmt.Println("Actual CHX Address = ", _addressFromPrivateKey)
	fmt.Println()

	// Signing example (use for signing Tx)
	msg := []byte("Chainium")
	networkCode := []byte("UNIT_TESTS") //TODO: replace with OWN_PUBLIC_BLOCKCHAIN_MAINNET for mainnet!
	sig := signMessage(networkCode, "B6WNNx9oK8qRUU52PpzjXHZuv4NUb3Z33hdju3hhrceS", msg)
	expectedSig := "6Hhxz2eP3AagR56mP4AAaKViUxHi3gM9c5weLDR48x4X4ynRBDfxsHGjhX9cni1mtCkNxbnZ783YPgMwVYV52X1w5"
	fmt.Println("SIGNING (tx)")
	fmt.Println("===========================================")
	fmt.Println("Expected Signature = ", expectedSig)
	fmt.Println("Actual Signature = ", sig)
	fmt.Println()

	// Signing plain text example
	txt := []byte("Chainium")
	privateKey = "3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq"
	expectedSig = "EzCsWgPozyVT9o6TycYV6q1n4YK4QWixa6Lk4GFvwrj6RU3K1wHcwNPZJUMBYcsGp5oFhytHiThon5zqE8uLk8naB"
	sig = signPlainText(privateKey, txt)
	fmt.Println("SIGNING (plainText)")
	fmt.Println("===========================================")
	fmt.Println("Expected Signature = ", expectedSig)
	fmt.Println("Actual Signature = ", sig)
	fmt.Println()

	// Verify plain text signature
	privateKey, address = generateWallet()
	expectedAddress := addressFromPrivateKey(privateKey)
	sig = signPlainText(privateKey, txt)
	address = verifyPlainTextSignature(sig, txt)
	fmt.Println("SIGNING (verifyPlainTextSign)")
	fmt.Println("===========================================")
	fmt.Println("Expected CHXAddress = ", expectedAddress)
	fmt.Println("Actual CHXAddress = ", address)
	fmt.Println()
}
