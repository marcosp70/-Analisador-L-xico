# Analisador Léxico

Implementação manual de um analisador léxico e de uma tabela de símbolos para a disciplina de Compiladores do curso de Engenharia de Computação do CEFET-MG.

## Integrantes

- Marcos Paulo Santos da Silva
- Arthur Bracarense
- Caio Rangel

## Funcionalidades

- reconhecimento dos tokens definidos pela linguagem;
- identificação de palavras reservadas e identificadores;
- instalação dos símbolos em tabela hash com ordem de inserção;
- registro de linha e coluna de cada token;
- mensagens de erro léxico com a posição do problema;
- comentários delimitados por `{*` e `*}`, inclusive em várias linhas;
- suporte opcional à procura de vários erros com `--all-errors`;
- diferenciação entre maiúsculas e minúsculas;
- testes automatizados sem dependências externas.

O analisador foi implementado diretamente em Java, sem JFlex, Lex, Flex, JavaCC ou outras ferramentas geradoras.

## Requisitos

- JDK 17 ou superior;
- PowerShell no Windows para utilizar os scripts `.ps1`.

Verifique a instalação do Java:

```powershell
java -version
javac -version
```

## Executando o JAR

O JAR executável está em `dist/compilador-etapa1.jar`.

```powershell
java -jar .\dist\compilador-etapa1.jar .\testes\corrigidos\teste1_corrigido.lpp
```

Também é possível utilizar o script:

```powershell
.\scripts\run.ps1 .\testes\corrigidos\teste1_corrigido.lpp
```

No Prompt de Comando:

```bat
run.bat testes\corrigidos\teste1_corrigido.lpp
```

Por padrão, o analisador encerra no primeiro erro léxico. Para procurar outros erros após a primeira ocorrência:

```powershell
java -jar .\dist\compilador-etapa1.jar --all-errors .\testes\originais\teste1.lpp
```

## Compilação e testes

```powershell
.\scripts\build.ps1
.\scripts\test.ps1
```

O primeiro comando compila o projeto e gera o JAR. O segundo realiza o build e executa os dez grupos de testes automatizados.

## Regras léxicas principais

- identificador: `[A-Za-z_][A-Za-z0-9_]*`;
- inteiro: `[0-9]+`;
- ponto flutuante: `[0-9]+\.[0-9]+`;
- caractere: um caractere ASCII entre aspas simples;
- literal: caracteres ASCII entre aspas duplas, sem quebra de linha;
- comentário: começa em `{*` e termina em `*}`;
- palavras reservadas são *case-sensitive*;
- `&&` e `||` devem aparecer completos;
- declarações utilizam `=` conforme `decl ::= ident-list "=" type`;
- `:` não pertence à linguagem e produz erro léxico.

## Saída

Para cada programa analisado são mostrados:

1. a sequência numerada de tokens, com lexema, atributo, linha e coluna;
2. a tabela de símbolos com posição, lexema, categoria e token;
3. o resultado final da análise léxica.

Identificadores e palavras reservadas utilizam como atributo a posição na tabela de símbolos, como em `<IDENTIFIER, TS[18]>`. As constantes carregam o próprio valor como atributo.

## Estrutura do projeto

```text
src/main/java/        código-fonte do compilador
src/test/java/        testes automatizados
testes/originais/     programas fornecidos no enunciado
testes/correcoes/     correções intermediárias dos erros léxicos
testes/corrigidos/    programas sem erros léxicos
testes/adicionais/    testes adicionais
dist/                 JAR executável
packaging/            manifesto utilizado na criação do JAR
scripts/              scripts de build, teste e execução
```

## Escopo

Este repositório corresponde à Etapa 1 do projeto. Erros sintáticos presentes nos programas de entrada são preservados quando não constituem erro léxico e serão tratados na etapa de análise sintática.
