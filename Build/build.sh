#$1 - path to solution folder
#$2 - build configuration Debug/Release etc. 
#$3 - output folder for each of the projects
dotnet build $1 -c $2 -o $3