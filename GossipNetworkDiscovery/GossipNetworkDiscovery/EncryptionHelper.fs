namespace Chainium.Common

open System
open System.IO
open System.Text
open System.Security.Cryptography

module EncryptionHelper = 
    let encrypt (value: string) password (salt : string) = 
        use rgb = new Rfc2898DeriveBytes(password, Encoding.Unicode.GetBytes(salt))

        let algorithm = new AesManaged()

        let rgbKey = algorithm.KeySize >>> 3 |> rgb.GetBytes        
        let rgbIV = algorithm.BlockSize >>> 3 |> rgb.GetBytes

        let transform = algorithm.CreateEncryptor(rgbKey, rgbIV)

        use buffer = new MemoryStream()
        ( 
            use stream = new CryptoStream(buffer, transform, CryptoStreamMode.Write)
            use writer = new StreamWriter(stream, Encoding.Unicode)
            writer.Write value           
        )
        buffer.ToArray() |> Convert.ToBase64String

    let decrypt text password (salt : string) = 
        use rgb = new Rfc2898DeriveBytes(password, Encoding.Unicode.GetBytes(salt))

        let algorithm = new AesManaged()

        let rgbKey = algorithm.KeySize >>> 3 |> rgb.GetBytes        
        let rgbIV = algorithm.BlockSize >>> 3 |> rgb.GetBytes

        let transform = algorithm.CreateDecryptor(rgbKey, rgbIV)
        use buffer = new MemoryStream(Convert.FromBase64String(text))
        (
            use stream = new CryptoStream(buffer, transform, CryptoStreamMode.Read)
            use reader = new StreamReader(stream, Encoding.Unicode)
            reader.ReadToEnd()
        )
