package br.cefetmg.compilador.lexer;

import java.util.Objects;

/** Token imutável com lexema, posição e atributo opcional. */
public final class Token {
    private final TokenType type;
    private final String lexeme;
    private final int line;
    private final int column;
    private final String attribute;

    public Token(TokenType type, String lexeme, int line, int column, String attribute) {
        this.type = Objects.requireNonNull(type);
        this.lexeme = Objects.requireNonNull(lexeme);
        this.line = line;
        this.column = column;
        this.attribute = attribute;
    }

    public TokenType type() {
        return type;
    }

    public String lexeme() {
        return lexeme;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public String attribute() {
        return attribute;
    }

    public String format() {
        String escaped = lexeme
                .replace("\\", "\\\\")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                .replace("\"", "\\\"");
        String pair = attribute == null
                ? "<" + type + ">"
                : "<" + type + ", " + attribute + ">";
        return String.format("%-28s lexema=\"%s\" linha=%d coluna=%d",
                pair, escaped, line, column);
    }

    @Override
    public String toString() {
        return format();
    }
}

