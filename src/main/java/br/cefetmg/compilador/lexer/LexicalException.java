package br.cefetmg.compilador.lexer;

// Erro lexico com a linha e a coluna onde o lexema comecou
public class LexicalException extends RuntimeException {
    private int line;
    private int column;
    private boolean endOfFile; // true quando o erro so foi percebido no fim do arquivo

    public LexicalException(String message, int line, int column, boolean endOfFile) {
        super(message);
        this.line = line;
        this.column = column;
        this.endOfFile = endOfFile;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public boolean isEndOfFile() {
        return endOfFile;
    }

    public String format() {
        return "Erro léxico na linha " + line + ", coluna " + column + ": " + getMessage();
    }
}
