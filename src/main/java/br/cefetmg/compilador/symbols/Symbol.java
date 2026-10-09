package br.cefetmg.compilador.symbols;

import br.cefetmg.compilador.lexer.TokenType;

// Uma entrada da tabela de simbolos
public class Symbol {
    public static final String PALAVRA_RESERVADA = "PALAVRA_RESERVADA";
    public static final String IDENTIFICADOR = "IDENTIFICADOR";

    private int index;
    private String lexeme;
    private String category;
    private TokenType tokenType;

    public Symbol(int index, String lexeme, String category, TokenType tokenType) {
        this.index = index;
        this.lexeme = lexeme;
        this.category = category;
        this.tokenType = tokenType;
    }

    public int getIndex() {
        return index;
    }

    public String getLexeme() {
        return lexeme;
    }

    public String getCategory() {
        return category;
    }

    public TokenType getTokenType() {
        return tokenType;
    }
}
