open Suave                 
open Suave.Successful      
open Suave.Web
open Suave.Filters 
open Suave.Operators 
open Newtonsoft.Json
open Newtonsoft.Json.Serialization
open Basic.Web.Api.Dtos


let defaultBase="/api/values"
let baseWithIdString=sprintf "%s/%s" defaultBase "%d"
let defaultBaseWithIdFormat() = PrintfFormat<_,_,_,_,int>(baseWithIdString)

let JSON v=
    let jsonSerializerSettings = new JsonSerializerSettings()
    jsonSerializerSettings.ContractResolver <- new CamelCasePropertyNamesContractResolver()
    
    JsonConvert.SerializeObject(v, jsonSerializerSettings)
    |> OK
    >=> Writers.setMimeType "application/json; charset=utf-8"

let fromJson<'a> json =
  JsonConvert.DeserializeObject(json, typeof<'a>) :?> 'a

let getResourceFromReq<'a> (req : HttpRequest) =
  let getString rawForm = System.Text.Encoding.UTF8.GetString(rawForm)
  req.rawForm |> getString |> fromJson<'a>

let getWithId id=JSON ({SampleItem.Id=id;Value="Item value"})

let values=[|for i in 1..2 -> sprintf "value%d" i|]

let printRequestObject<'a> request=
    let item=getResourceFromReq<'a> request
    sprintf "%A" item

let printIdAndSample id sample=sprintf "test %d %A" id sample


let processPutRequest id request=
    let sample=getResourceFromReq<SampleItem> request in ()
    let strVal=printIdAndSample id sample
    JSON strVal

let processDelete id=(sprintf "Item %d has been deleted" id) |> OK
                             


let webPart = 
    choose  [
        GET >=> choose [ 
            path defaultBase >=> JSON values
            pathScan (defaultBaseWithIdFormat ()) (fun id->getWithId id)
        ]
        POST >=>choose [
            path defaultBase >=> request(getResourceFromReq<SampleItem> >> JSON)
        ]
        PUT >=> choose [
            pathScan (defaultBaseWithIdFormat ()) (fun id->request(fun r->processPutRequest id r))
        ]
        DELETE >=> choose [
            pathScan (defaultBaseWithIdFormat ()) (fun id -> processDelete id)
        ]
    ]

startWebServer defaultConfig webPart