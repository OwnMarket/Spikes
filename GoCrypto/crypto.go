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
	"github.com/tyler-smith/go-bip32"
	"github.com/tyler-smith/go-bip39"
)

////////////////////////////////////////////////////////////////////////////////////////////////////
// Types
////////////////////////////////////////////////////////////////////////////////////////////////////

type WalletInfo struct {
	PrivateKey string
	Address    string
}

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

func generateWallet() *WalletInfo {
	key, err := ecdsa.GenerateKey(secp256k1.S256(), rand.Reader)
	if err != nil {
		panic(err)
	}

	publicKey := elliptic.Marshal(secp256k1.S256(), key.X, key.Y)

	privateKeyBytes := make([]byte, 32)
	blob := key.D.Bytes()
	copy(privateKeyBytes[32-len(blob):], blob)

	wallet := &WalletInfo{
		PrivateKey: encode58(privateKeyBytes),
		Address:    blockchainAddress(publicKey),
	}
	return wallet
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

////////////////////////////////////////////////////////////////////////////////////////////////////
// Hierarchical Deterministic Cryptography
////////////////////////////////////////////////////////////////////////////////////////////////////

func generateMnemonic() string {
	entropy, _ := bip39.NewEntropy(256)
	mnemonic, _ := bip39.NewMnemonic(entropy)
	return mnemonic
}

func generateSeedFromMnemonic(mnemonic string, passphrase string) []byte {
	if !bip39.IsMnemonicValid(mnemonic) {
		panic("Invalid mnemonic")
	}
	return bip39.NewSeed(mnemonic, passphrase)
}

func generateSeedFromKeystore(keyStoreEncrypted []byte, passwordHash [32]byte) []byte {
	return decrypt(keyStoreEncrypted, passwordHash)
}

func generateMasterKeyFromSeed(seed []byte) *bip32.Key {
	masterKey, _ := bip32.NewMasterKey(seed)
	return masterKey
}

func generateKeystore(mnemonic string, passwordHash [32]byte) []byte {
	seed := generateSeedFromMnemonic(mnemonic, "")
	return encrypt(seed, passwordHash)
}

// m/44'/{coin}'/0'/0/${address}
func newKeyFromMasterKey(masterKey *bip32.Key, coin, address uint32) (*bip32.Key, error) {
	const _purpose uint32 = 44
	const _account = 0
	const _chain = 0

	child, err := masterKey.NewChildKey(bip32.FirstHardenedChild + 44) // m/44'
	if err != nil {
		return nil, err
	}

	child, err = child.NewChildKey(bip32.FirstHardenedChild + coin) // m/44'/${coin}'
	if err != nil {
		return nil, err
	}

	child, err = child.NewChildKey(bip32.FirstHardenedChild + _account) // m/44'/${coin}'/0'
	if err != nil {
		return nil, err
	}

	child, err = child.NewChildKey(_chain) // m/44'/${coin}'/0'/0
	if err != nil {
		return nil, err
	}

	child, err = child.NewChildKey(address) // m/44'/${coin}'/0'/0/${address}
	if err != nil {
		return nil, err
	}

	return child, nil
}

func generateWalletFromSeedWithExplicitCoinIndex(seed []byte, coin uint32, keyIndex uint32) *WalletInfo {
	masterKey := generateMasterKeyFromSeed(seed)
	childKey, err := newKeyFromMasterKey(masterKey, coin, keyIndex)
	if err != nil {
		fmt.Println(err)
	}
	privateKeyBytes := childKey.Key
	privateKey := encode58(privateKeyBytes)
	wallet := &WalletInfo{
		PrivateKey: privateKey,
		Address:    addressFromPrivateKey(privateKey),
	}
	return wallet
}

func generateWalletFromSeed(seed []byte, keyIndex uint32) *WalletInfo {
	return generateWalletFromSeedWithExplicitCoinIndex(seed, 25718, keyIndex)
}

func restoreWalletsFromSeed(seed []byte, walletCount uint32) [](*WalletInfo) {
	var wallets [](*WalletInfo)
	var i uint32 = 0
	for ; i < walletCount; i++ {
		wallet := generateWalletFromSeed(seed, i)
		wallets = append(wallets, wallet)
	}
	return wallets
}

func generateWalletFromKeystore(keyStoreEncrypted []byte, passwordHash [32]byte, keyIndex uint32) *WalletInfo {
	seed := generateSeedFromKeystore(keyStoreEncrypted, passwordHash)
	return generateWalletFromSeed(seed, keyIndex)
}

func restoreWalletsFromKeystore(keyStoreEncrypted []byte, passwordHash [32]byte, walletCount uint32) [](*WalletInfo) {
	seed := generateSeedFromKeystore(keyStoreEncrypted, passwordHash)
	return restoreWalletsFromSeed(seed, walletCount)
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
	wallet := generateWallet()
	_addressFromPrivateKey := addressFromPrivateKey(wallet.PrivateKey)
	fmt.Println("WALLET")
	fmt.Println("===========================================")
	fmt.Println("Expected CHX Address = ", wallet.Address)
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
	privateKey := "3rzY3EENhYrWXzUqNnMEbGUr3iEzzSZrjMwJ1CgQpJpq"
	expectedSig = "EzCsWgPozyVT9o6TycYV6q1n4YK4QWixa6Lk4GFvwrj6RU3K1wHcwNPZJUMBYcsGp5oFhytHiThon5zqE8uLk8naB"
	sig = signPlainText(privateKey, txt)
	fmt.Println("SIGNING (plainText)")
	fmt.Println("===========================================")
	fmt.Println("Expected Signature = ", expectedSig)
	fmt.Println("Actual Signature = ", sig)
	fmt.Println()

	// Verify plain text signature
	wallet = generateWallet()
	expectedAddress := addressFromPrivateKey(wallet.PrivateKey)
	sig = signPlainText(wallet.PrivateKey, txt)
	address := verifyPlainTextSignature(sig, txt)
	fmt.Println("SIGNING (verifyPlainTextSign)")
	fmt.Println("===========================================")
	fmt.Println("Expected CHXAddress = ", expectedAddress)
	fmt.Println("Actual CHXAddress = ", address)
	fmt.Println()

	// HD Crypto
	mnemonic := "receive raccoon rocket donkey cherry garbage medal skirt random smoke young before scale leave hold insect foster blouse mail donkey regular vital hurt april"
	seed := generateSeedFromMnemonic(mnemonic, "")
	wallet = generateWalletFromSeed(seed, 0)
	wallets := restoreWalletsFromSeed(seed, 1)
	expectedPrivateKey := "ECPVXjz78oMdmLKbHVAAo7X7evtTh4EfnaW5Yc1SHWaj"
	expectedAddress = "CHb5Z6Za34nv28Z3rLZ2Yd8LFikHaTqLhxB"
	fmt.Println("HD Crypto")
	fmt.Println("===========================================")
	fmt.Println("Expected PrivateKey = ", expectedPrivateKey)
	fmt.Println("Actual PrivateKey = ", wallet.PrivateKey)
	fmt.Println("Restored PrivateKey = ", wallets[0].PrivateKey)
	fmt.Println("Expected CHXAddress = ", expectedAddress)
	fmt.Println("Actual CHXAddress = ", wallet.Address)
	fmt.Println("Restored CHXAddress = ", wallets[0].Address)
}
