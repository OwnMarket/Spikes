namespace MessagePackFsharpReview.Tests

open Microsoft.VisualStudio.TestTools.UnitTesting
open MessagePack
open MessagePackFSharpReview.DomainTypes
open MessagePack.Resolvers
open MessagePack.FSharp

module UnitTests =

    [<TestClass>]
    type TestClass () =

        [<TestMethod>]
        member __.TestDUSerialization () =

            CompositeResolver.RegisterAndSetAsDefault(
                FSharpResolver.Instance,
                StandardResolver.Instance
            )

            let data = Foo 999
            let bin = MessagePackSerializer.Serialize(data)

            match MessagePackSerializer.Deserialize<UnionSample>(bin) with
            | Foo _ -> Assert.IsTrue(true);
            | Bar _ -> Assert.Fail();


