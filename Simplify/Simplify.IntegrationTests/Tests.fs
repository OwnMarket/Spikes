module Tests

open System
open Xunit
open Swensen.Unquote

[<Fact>]
let ``Simplify - integration test`` () =
    Assert.True(true)

[<Fact>]
let ``Simplify - additional integration test `` () =
    test <@ true = true @>
