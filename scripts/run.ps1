param(
    [Parameter(Mandatory = $true, Position = 0)]
    [string]$Arquivo,
    [switch]$TodosErros
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jarPath = Join-Path $projectRoot 'dist\compilador-etapa1.jar'

if (-not (Test-Path $jarPath)) {
    & (Join-Path $PSScriptRoot 'build.ps1')
}

$arguments = @('-jar', $jarPath)
if ($TodosErros) { $arguments += '--all-errors' }
$arguments += $Arquivo
& java $arguments
exit $LASTEXITCODE

