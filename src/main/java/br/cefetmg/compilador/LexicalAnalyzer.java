package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.Lexer;
import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.lexer.TokenType;
import br.cefetmg.compilador.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;

// Chama o lexer ate chegar no fim do arquivo.
// Por padrao para no primeiro erro. Com allErrors = true continua depois do erro
// (modo panico simples: o lexer ja descartou o lexema com problema).
public class LexicalAnalyzer {

    public AnalysisResult analyze(String source, boolean allErrors) {
        SymbolTable symbolTable = new SymbolTable();
        Lexer lexer = new Lexer(source, symbolTable);
        List<Token> tokens = new ArrayList<>();
        List<LexicalException> errors = new ArrayList<>();

        boolean stop = false;
        while (!stop) {
            try {
                Token token = lexer.nextToken();
                tokens.add(token);
                if (token.getType() == TokenType.EOF) {
                    stop = true;
                }
            } catch (LexicalException e) {
                errors.add(e);
                // erro no fim do arquivo (ex.: comentario nao fechado) nao tem como continuar
                if (!allErrors || e.isEndOfFile()) {
                    stop = true;
                }
            }
        }

        return new AnalysisResult(tokens, errors, symbolTable);
    }
}
