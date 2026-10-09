package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.symbols.Symbol;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

// Etapa 1 do compilador: analisador lexico e tabela de simbolos
// Uso: java -jar compilador-etapa1.jar [--all-errors] <arquivo-fonte>
public class Main {

    public static void main(String[] args) {
        // para os acentos aparecerem certo no terminal
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));

        boolean allErrors = false;
        String fileName = null;
        int fileCount = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("--all-errors")) {
                allErrors = true;
            } else if (args[i].equals("--help") || args[i].equals("-h")) {
                printUsage();
                return;
            } else if (args[i].startsWith("-")) {
                System.err.println("Opção desconhecida: " + args[i]);
                printUsage();
                System.exit(2);
            } else {
                fileName = args[i];
                fileCount++;
            }
        }

        if (fileCount != 1) {
            printUsage();
            System.exit(2);
        }

        Path path = Paths.get(fileName);
        String source;
        try {
            source = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Não foi possível ler o arquivo: " + path);
            System.err.println(e.getMessage());
            System.exit(2);
            return;
        }

        LexicalAnalyzer analyzer = new LexicalAnalyzer();
        AnalysisResult result = analyzer.analyze(source, allErrors);
        printResult(path, result);

        if (!result.isSuccess()) {
            System.exit(1);
        }
    }

    private static void printResult(Path path, AnalysisResult result) {
        System.out.println("Arquivo: " + path.toAbsolutePath().normalize());
        System.out.println();

        System.out.println("=== SEQUÊNCIA DE TOKENS ===");
        List<Token> tokens = result.getTokens();
        for (int i = 0; i < tokens.size(); i++) {
            System.out.printf("%03d  %s%n", i + 1, tokens.get(i).format());
        }
        System.out.println();

        System.out.println("=== TABELA DE SÍMBOLOS ===");
        System.out.printf("%-5s %-24s %-20s %s%n", "Pos.", "Lexema", "Categoria", "Token");
        for (Symbol s : result.getSymbolTable().getSymbols()) {
            System.out.printf("%-5d %-24s %-20s %s%n",
                    s.getIndex(), s.getLexeme(), s.getCategory(), s.getTokenType());
        }
        System.out.println();

        if (result.isSuccess()) {
            System.out.println("Resultado: SUCESSO - análise léxica concluída sem erros.");
        } else {
            System.out.println("=== ERROS LÉXICOS ===");
            for (LexicalException e : result.getErrors()) {
                System.out.println(e.format());
            }
            System.out.println("Resultado: FALHA - " + result.getErrors().size()
                    + " erro(s) léxico(s) encontrado(s).");
        }
    }

    private static void printUsage() {
        System.out.println("Uso: java -jar compilador-etapa1.jar [--all-errors] <arquivo-fonte>");
        System.out.println("  --all-errors  continua a análise depois de um erro para listar vários erros léxicos");
        System.out.println("  sem a opção, o analisador encerra no primeiro erro");
    }
}
