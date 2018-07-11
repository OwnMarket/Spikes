namespace Chainium.Common

open System
open System.Security.Cryptography

module Map =

    let inline ofDict dictionary =
        dictionary
        |> Seq.map (|KeyValue|)
        |> Map.ofSeq

module Seq =

    let inline shuffleR (r : Random) xs = 
        xs |> Seq.sortBy (fun _ -> r.Next())

    let inline shuffleG xs = 
        xs |> Seq.sortBy (fun _ -> Guid.NewGuid())

    let shuffleCrypto xs =
        let a = xs |> Seq.toArray

        use rng = new RNGCryptoServiceProvider ()
        let bytes = Array.zeroCreate a.Length
        rng.GetBytes bytes

        Array.zip bytes a |> Array.sortBy fst |> Array.map snd
