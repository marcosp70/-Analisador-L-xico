package br.cefetmg.compilador.symbols;

import br.cefetmg.compilador.lexer.TokenType;

/** Entrada da tabela de símbolos. */
public record Symbol(int index, String lexeme, Category category, TokenType tokenType) {
    public enum Category {
        PALAVRA_RESERVADA,
        IDENTIFICADOR
    }
}

