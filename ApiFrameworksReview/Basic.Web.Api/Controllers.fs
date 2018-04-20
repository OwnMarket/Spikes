 namespace Basic.Web.Api.Controllers

    open Microsoft.AspNetCore.Mvc
    open Basic.Web.Api.Dtos


    

    [<Route(template="api/[controller]")>]
    type ValuesController()=
        inherit ControllerBase()
        
        [<HttpGet>]
        member this.Get()=
            [|for i in 1..2 -> sprintf "value%d" i|]

        [<HttpGet("{id}")>]
        member this.Get id=
             JsonResult({SampleItem.Id=id;Value="Item value"})
        
        [<HttpPost>]        
        member this.Post ([<FromBody>](item:SampleItem))=
            this.Ok(sprintf "%A" item)

        [<HttpPut("{id}")>]
        member this.Put id ([<FromBody>](value:SampleItem))=
            this.Ok(sprintf "test %d %A" id value)

        [<HttpDelete("{id}")>]
        member this.Delete (id:int)=
            sprintf "Item %d has been deleted" id