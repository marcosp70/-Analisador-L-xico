package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.symbols.Symbol;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Ponto de entrada da Etapa 1 do compilador. */
public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        configureUtf8Console();
        int status = run(args, System.out, System.err);
        if (status != 0) {
            System.exit(status);
        }
    }

    static int run(String[] args, PrintStream out, PrintStream err) {
        boolean collectAllErrors = false;
        List<String> positional = new ArrayList<>();

        for (String arg : args) {
            if ("--all-errors".equals(arg)) {
                collectAllErrors = true;
            } else if ("--help".equals(arg) || "-h".equals(arg)) {
                printUsage(out);
                return 0;
            } else if (arg.startsWith("-")) {
                err.println("Opção desconhecida: " + arg);
                printUsage(err);
                return 2;
            } else {
                positional.add(arg);
            }
        }

        if (positional.size() != 1) {
            printUsage(err);
            return 2;
        }

        Path sourcePath = Path.of(positional.get(0));
        String source;
        try {
            source = Files.readString(sourcePath, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            err.println("Não foi possível ler o arquivo: " + sourcePath);
            err.println(exception.getMessage());
            return 2;
        }

        AnalysisResult result = new LexicalAnalyzer().analyze(source, collectAllErrors);
        printResult(sourcePath, result, out);
        return result.successful() ? 0 : 1;
    }

    private static void printResult(Path sourcePath, AnalysisResult result, PrintStream out) {
        out.println("Arquivo: " + sourcePath.toAbsolutePath().normalize());
        out.println();
        out.println("=== SEQUÊNCIA DE TOKENS ===");
        int number = 1;
        for (Token token : result.tokens()) {
            out.printf("%03d  %s%n", number++, token.format());
        }

        out.println();
        out.println("=== TABELA DE SÍMBOLOS ===");
        out.printf("%-5s %-24s %-20s %s%n", "Pos.", "Lexema", "Categoria", "Token");
        for (Symbol symbol : result.symbolTable().symbols()) {
            out.printf("%-5d %-24s %-20s %s%n",
                    symbol.index(), symbol.lexeme(), symbol.category(), symbol.tokenType());
        }

        out.println();
        if (result.successful()) {
            out.println("Resultado: SUCESSO - análise léxica concluída sem erros.");
        } else {
            out.println("=== ERROS LÉXICOS ===");
            for (LexicalException error : result.errors()) {
                out.println(error.format());
            }
            out.printf("Resultado: FALHA - %d erro(s) léxico(s) encontrado(s).%n",
                    result.errors().size());
        }
    }

    private static void printUsage(PrintStream stream) {
        stream.println("Uso: java -jar compilador-etapa1.jar [--all-errors] <arquivo-fonte>");
        stream.println("  --all-errors  aplica recuperação simples para listar vários erros léxicos");
        stream.println("  sem a opção, o analisador encerra no primeiro erro");
    }

    private static void configureUtf8Console() {
        try {
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
            System.setErr(new PrintStream(System.err, true, StandardCharsets.UTF_8));
        } catch (Exception ignored) {
            // O programa continua com a codificação padrão se a JVM não permitir a troca.
        }
    }
}

