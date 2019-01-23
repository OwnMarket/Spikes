namespace MessagePackFSharpReview.DomainTypes

open MessagePack

[<MessagePackObject>]
type UnionSample =
    | Foo of XYZ : int
    | Bar of OPQ : string list
