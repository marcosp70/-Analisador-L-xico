package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.LexicalException;
import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.symbols.SymbolTable;

import java.util.List;

/** Resultado completo de uma execução do analisador léxico. */
public record AnalysisResult(List<Token> tokens, List<LexicalException> errors,
                             SymbolTable symbolTable) {
    public boolean successful() {
        return errors.isEmpty();
    }
}

