package br.cefetmg.compilador.lexer;

/** Erro léxico com a posição inicial e indicação de fim de arquivo. */
public final class LexicalException extends RuntimeException {
    private final int line;
    private final int column;
    private final boolean atEndOfFile;

    public LexicalException(String message, int line, int column, boolean atEndOfFile) {
        super(message);
        this.line = line;
        this.column = column;
        this.atEndOfFile = atEndOfFile;
    }

    public int line() {
        return line;
    }

    public int column() {
        return column;
    }

    public boolean atEndOfFile() {
        return atEndOfFile;
    }

    public String format() {
        return String.format("Erro léxico na linha %d, coluna %d: %s",
                line, column, getMessage());
    }
}

