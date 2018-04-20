    open Microsoft.AspNetCore.Hosting
    open Microsoft.AspNetCore.Builder
    open Microsoft.Extensions.DependencyInjection    

    type Startup()=
        member this.Configure (app:IApplicationBuilder) (env:IHostingEnvironment)=
            app.UseMvc() |> ignore

        member this.ConfigureServices (services:IServiceCollection) =
            let mvcCoreBuilder=services.AddMvcCore()
            mvcCoreBuilder.AddFormatterMappings()
                .AddJsonFormatters()
                .AddCors() |> ignore

    [<EntryPoint>]
    let main _ =
        WebHostBuilder()
            .UseKestrel()
            .UseStartup<Startup>()
            .Build()
            .Run()
        0
