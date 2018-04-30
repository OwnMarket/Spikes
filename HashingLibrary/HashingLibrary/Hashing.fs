namespace HashingLibrary

module Hashing =
    open System.Security.Cryptography
    (* Neo is using same hashing library implemented in .NET core *)
    let baseHash()=SHA256.Create() :> HashAlgorithm

    (* Get base hash,in this case it is SHA256, but it can be easily replaced
    This function is intended to be used for all hashing except for address hashing*)
    let getHash (dataToHash:byte[])=
        baseHash().ComputeHash(dataToHash)
    
    (* only to be used for address hashing
        it uses SHA256, and then uses SHA512 and takes first 20 bytes from resulting hash
        Ripple, Bitcoin and Neo do the same thing, second hashing is done using RIPEMD160
        but that algorithm is not implemented in .NET core, and SHA512 is better hashing algorithm *)
    let getAddressHash (dataToHash:byte[])=
        let numOfBytesToTake=20;
        
        let sha160Hash=fun (data:byte[]) -> let sha512=SHA512.Create() in () ;  sha512.ComputeHash(data) |> Array.take(numOfBytesToTake)
        
        let sha256=SHA256.Create()
        dataToHash |> sha256.ComputeHash |> sha160Hash
