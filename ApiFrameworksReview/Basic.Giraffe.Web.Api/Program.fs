open Giraffe
open Microsoft.AspNetCore.Builder
open Microsoft.Extensions.DependencyInjection
open Microsoft.AspNetCore.Hosting
open System
open Microsoft.AspNetCore.Http
open Basic.Web.Api.Dtos

let defaultBase="/api/values"
let baseWithIdString=sprintf "%s/%s" defaultBase "%i"
let defaultBaseWithIdFormat() = PrintfFormat<_,_,_,_,int>(baseWithIdString)


let postHandler=fun (next : HttpFunc) (ctx : HttpContext) ->
        task {
            let! item=ctx.BindJsonAsync<SampleItem>()
            return! text (sprintf "%A" item) next ctx
        }

let putHandler=fun id (next:HttpFunc) (ctx:HttpContext) ->
    task {
        let! item=ctx.BindJsonAsync<SampleItem>()
        return! text (sprintf "test %d %A" id item) next ctx
    }

let webApp =
    choose [
        GET >=>
            choose [
                route defaultBase >=> json [for i in [1..2] -> sprintf "value%d" i]
            ]
        POST >=>
            choose [
                route defaultBase >=> postHandler
            ]
        PUT >=>
            choose[
                routef (defaultBaseWithIdFormat ()) putHandler
            ]
        DELETE >=>
            choose[
                 routef (defaultBaseWithIdFormat ()) (fun id->(sprintf "Item %d has been deleted" id) |> text)
            ]
        setStatusCode 404 >=> text "Not Found" ]

let configureApp (app : IApplicationBuilder) =
    // Add Giraffe to the ASP.NET Core pipeline
    app.UseGiraffe webApp

let configureServices (services : IServiceCollection) =
    // Add Giraffe dependencies
    services.AddGiraffe() |> ignore

[<EntryPoint>]
let main _ =
    WebHostBuilder()
        .UseKestrel()
        .Configure(Action<IApplicationBuilder> configureApp)
        .ConfigureServices(configureServices)
        .Build()
        .Run()
    0


