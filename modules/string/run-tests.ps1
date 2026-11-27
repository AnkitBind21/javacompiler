# Compile the String module + tests and run them. Needs a JDK (javac).
Set-Location $PSScriptRoot
if (Test-Path out) { Remove-Item -Recurse -Force out }
New-Item -ItemType Directory out | Out-Null

$files = Get-ChildItem -Recurse -Path src, test -Filter *.java | ForEach-Object { $_.FullName }
javac -d out $files
if ($LASTEXITCODE -ne 0) { exit 1 }

java -cp out compiler.str.StrTests
exit $LASTEXITCODE
