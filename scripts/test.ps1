$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'build.ps1')
& java -ea -cp "$(Join-Path $projectRoot 'build\classes');$(Join-Path $projectRoot 'build\test-classes')" br.cefetmg.compilador.LexerSelfTest
if ($LASTEXITCODE -ne 0) { throw 'Os testes automatizados falharam.' }

