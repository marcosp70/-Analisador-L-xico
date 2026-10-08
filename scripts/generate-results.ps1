$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
$jarPath = Join-Path $projectRoot 'dist\compilador-etapa1.jar'
$resultsRoot = Join-Path $projectRoot 'resultados'

if (-not (Test-Path $jarPath)) {
    & (Join-Path $PSScriptRoot 'build.ps1')
}

New-Item -ItemType Directory -Force $resultsRoot | Out-Null

$scenarios = @(
    @('teste1_00_original', 'testes\originais\teste1.lpp'),
    @('teste1_01_corrige_primeiro_dois_pontos', 'testes\correcoes\teste1_passo1.lpp'),
    @('teste1_02_corrige_segundo_dois_pontos', 'testes\correcoes\teste1_passo2.lpp'),
    @('teste1_03_corrige_1a', 'testes\correcoes\teste1_passo3.lpp'),
    @('teste1_04_corrige_2base', 'testes\correcoes\teste1_passo4.lpp'),
    @('teste1_05_corrige_2altura', 'testes\correcoes\teste1_passo5.lpp'),
    @('teste1_06_corrigido', 'testes\corrigidos\teste1_corrigido.lpp'),
    @('teste2_00_original', 'testes\originais\teste2.lpp'),
    @('teste2_01_corrige_1c', 'testes\correcoes\teste2_passo1.lpp'),
    @('teste2_02_corrige_primeiro_dois_pontos', 'testes\correcoes\teste2_passo2.lpp'),
    @('teste2_03_corrige_segundo_dois_pontos', 'testes\correcoes\teste2_passo3.lpp'),
    @('teste2_04_corrige_atribuicao', 'testes\correcoes\teste2_passo4.lpp'),
    @('teste2_05_corrigido', 'testes\corrigidos\teste2_corrigido.lpp'),
    @('teste3_00_original', 'testes\originais\teste3.lpp'),
    @('teste3_01_corrige_primeiro_dois_pontos', 'testes\correcoes\teste3_passo1.lpp'),
    @('teste3_02_corrige_segundo_dois_pontos', 'testes\correcoes\teste3_passo2.lpp'),
    @('teste3_03_corrigido', 'testes\corrigidos\teste3_corrigido.lpp'),
    @('teste4_00_original', 'testes\originais\teste4.lpp'),
    @('teste4_01_fecha_comentario', 'testes\correcoes\teste4_passo1.lpp'),
    @('teste4_02_corrige_primeiro_dois_pontos', 'testes\correcoes\teste4_passo2.lpp'),
    @('teste4_03_corrige_segundo_dois_pontos', 'testes\correcoes\teste4_passo3.lpp'),
    @('teste4_04_corrige_identificador', 'testes\correcoes\teste4_passo4.lpp'),
    @('teste4_05_corrigido', 'testes\corrigidos\teste4_corrigido.lpp'),
    @('teste5_00_original', 'testes\originais\teste5.lpp'),
    @('teste5_01_corrige_primeiro_dois_pontos', 'testes\correcoes\teste5_passo1.lpp'),
    @('teste5_02_corrige_segundo_dois_pontos', 'testes\correcoes\teste5_passo2.lpp'),
    @('teste5_03_corrigido', 'testes\corrigidos\teste5_corrigido.lpp'),
    @('teste6a_adicional', 'testes\adicionais\teste6a_todos_tokens.lpp'),
    @('teste6b_adicional', 'testes\adicionais\teste6b_case_sensitive.lpp')
)

foreach ($scenario in $scenarios) {
    $name = $scenario[0]
    $sourcePath = Join-Path $projectRoot $scenario[1]
    $outputPath = Join-Path $resultsRoot ($name + '.txt')
    $output = & java -jar $jarPath $sourcePath 2>&1 | Out-String
    [System.IO.File]::WriteAllText($outputPath, $output, [System.Text.UTF8Encoding]::new($false))
}

Write-Host "Resultados gerados em: $resultsRoot"

