$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$sourceRoot = Join-Path $projectRoot 'src\main\java'
$testRoot = Join-Path $projectRoot 'src\test\java'
$classes = Join-Path $projectRoot 'build\classes'
$testClasses = Join-Path $projectRoot 'build\test-classes'
$dist = Join-Path $projectRoot 'dist'

if (-not (Get-Command javac -ErrorAction SilentlyContinue)) {
    throw 'javac não encontrado. Instale o JDK 17 ou superior e configure JAVA_HOME/PATH.'
}

New-Item -ItemType Directory -Force $classes, $testClasses, $dist | Out-Null
Remove-Item -Recurse -Force $classes, $testClasses
New-Item -ItemType Directory -Force $classes, $testClasses | Out-Null

$mainSources = @(Get-ChildItem $sourceRoot -Recurse -Filter '*.java' | ForEach-Object FullName)
$testSources = @(Get-ChildItem $testRoot -Recurse -Filter '*.java' | ForEach-Object FullName)

& javac -encoding UTF-8 --release 17 -d $classes $mainSources
if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar o código principal.' }

& javac -encoding UTF-8 --release 17 -cp $classes -d $testClasses $testSources
if ($LASTEXITCODE -ne 0) { throw 'Falha ao compilar os testes.' }

$jarPath = Join-Path $dist 'compilador-etapa1.jar'
& jar --create --file $jarPath --manifest (Join-Path $projectRoot 'packaging\MANIFEST.MF') -C $classes .
if ($LASTEXITCODE -ne 0) { throw 'Falha ao gerar o JAR.' }

Write-Host "Build concluído: $jarPath"

