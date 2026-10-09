package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.symbols.SymbolTable;

import java.util.List;

// Guarda tudo que a analise lexica produziu: tokens, erros e tabela de simbolos
public class AnalysisResult {
    private List<Token> tokens;
    private List<LexicalException> errors;
    private SymbolTable symbolTable;

    public AnalysisResult(List<Token> tokens, List<LexicalException> errors, SymbolTable symbolTable) {
        this.tokens = tokens;
        this.errors = errors;
        this.symbolTable = symbolTable;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public List<LexicalException> getErrors() {
        return errors;
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }
}
