$ErrorActionPreference = 'Continue'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jarPath = Join-Path $projectRoot 'dist\compilador-etapa1.jar'

if (-not (Test-Path $jarPath)) {
    & (Join-Path $PSScriptRoot 'build.ps1')
}

Get-ChildItem (Join-Path $projectRoot 'testes') -Recurse -Filter '*.lpp' |
    Sort-Object FullName |
    ForEach-Object {
        Write-Host "`n===== $($_.FullName.Substring($projectRoot.Length + 1)) ====="
        & java -jar $jarPath --all-errors $_.FullName
    }

