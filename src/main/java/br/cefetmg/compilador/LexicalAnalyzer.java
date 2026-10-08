package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.Lexer;
import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.lexer.TokenType;
import br.cefetmg.compilador.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/** Coordena o lexer e implementa os modos de parada imediata e modo pânico. */
public final class LexicalAnalyzer {
    public AnalysisResult analyze(String source, boolean collectAllErrors) {
        SymbolTable symbolTable = new SymbolTable();
        Lexer lexer = new Lexer(source, symbolTable);
        List<Token> tokens = new ArrayList<>();
        List<LexicalException> errors = new ArrayList<>();

        while (true) {
            try {
                Token token = lexer.nextToken();
                tokens.add(token);
                if (token.type() == TokenType.EOF) {
                    break;
                }
            } catch (LexicalException exception) {
                errors.add(exception);
                if (!collectAllErrors || exception.atEndOfFile()) {
                    break;
                }
            }
        }

        return new AnalysisResult(List.copyOf(tokens), List.copyOf(errors), symbolTable);
    }
}

