package br.cefetmg.compilador.lexer;

public class Token {
    private TokenType type;
    private String lexeme;
    private int line;
    private int column;
    private String attribute; // pode ser null quando o token nao tem atributo

    public Token(TokenType type, String lexeme, int line, int column, String attribute) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.attribute = attribute;
    }

    public TokenType getType() {
        return type;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public String getAttribute() {
        return attribute;
    }

    // formato: <TIPO, atributo>  lexema="..." linha=X coluna=Y
    public String format() {
        String pair;
        if (attribute == null) {
            pair = "<" + type + ">";
        } else {
            pair = "<" + type + ", " + attribute + ">";
        }
        String text = lexeme.replace("\\", "\\\\");
        text = text.replace("\t", "\\t");
        text = text.replace("\"", "\\\"");
        return String.format("%-28s lexema=\"%s\" linha=%d coluna=%d", pair, text, line, column);
    }

    @Override
    public String toString() {
        return format();
    }
}
