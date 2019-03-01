namespace SocketsFsharp

open System

[<AutoOpen>]
module Extensions =

    type String with
        member this.IsNullOrEmpty() = String.IsNullOrEmpty(this)
        member this.IsNullOrWhiteSpace() = String.IsNullOrWhiteSpace(this)