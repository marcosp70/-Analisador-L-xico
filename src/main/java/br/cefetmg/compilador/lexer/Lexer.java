package br.cefetmg.compilador.lexer;

import br.cefetmg.compilador.symbols.Symbol;
import br.cefetmg.compilador.symbols.SymbolTable;

import java.util.Objects;

/**
 * Analisador léxico manual. O reconhecimento é codificado diretamente a partir
 * dos diagramas de transição, sem geradores de analisadores.
 */
public final class Lexer {
    private final String source;
    private final SymbolTable symbolTable;
    private int index = 0;
    private int line = 1;
    private int column = 1;

    public Lexer(String source, SymbolTable symbolTable) {
        this.source = Objects.requireNonNull(source);
        this.symbolTable = Objects.requireNonNull(symbolTable);
    }

    public Token nextToken() {
        skipIgnored();

        if (isAtEnd()) {
            return new Token(TokenType.EOF, "", line, column, null);
        }

        int start = index;
        int startLine = line;
        int startColumn = column;
        char c = advance();

        if (isAsciiLetter(c) || c == '_') {
            return scanIdentifier(start, startLine, startColumn);
        }

        if (Character.isLetter(c) || Character.isDigit(c) && !isAsciiDigit(c)) {
            consumeUnicodeWordTail();
            String invalid = source.substring(start, index);
            throw error("identificador contém caractere fora de [A-Za-z0-9_]: '"
                    + printable(invalid) + "'", startLine, startColumn, false);
        }

        if (isAsciiDigit(c)) {
            return scanNumber(start, startLine, startColumn);
        }

        return switch (c) {
            case '\'' -> scanChar(startLine, startColumn);
            case '"' -> scanLiteral(startLine, startColumn);
            case '=' -> match('=')
                    ? token(TokenType.EQUAL, start, startLine, startColumn, null)
                    : token(TokenType.ASSIGN, start, startLine, startColumn, null);
            case '>' -> match('=')
                    ? token(TokenType.GREATER_EQUAL, start, startLine, startColumn, null)
                    : token(TokenType.GREATER, start, startLine, startColumn, null);
            case '<' -> match('=')
                    ? token(TokenType.LESS_EQUAL, start, startLine, startColumn, null)
                    : token(TokenType.LESS, start, startLine, startColumn, null);
            case '!' -> match('=')
                    ? token(TokenType.NOT_EQUAL, start, startLine, startColumn, null)
                    : token(TokenType.NOT, start, startLine, startColumn, null);
            case '|' -> {
                if (!match('|')) {
                    throw error("operador '|' incompleto; use '||'", startLine, startColumn, false);
                }
                yield token(TokenType.OR, start, startLine, startColumn, null);
            }
            case '&' -> {
                if (!match('&')) {
                    throw error("operador '&' incompleto; use '&&'", startLine, startColumn, false);
                }
                yield token(TokenType.AND, start, startLine, startColumn, null);
            }
            case '+' -> token(TokenType.PLUS, start, startLine, startColumn, null);
            case '-' -> token(TokenType.MINUS, start, startLine, startColumn, null);
            case '*' -> token(TokenType.MULTIPLY, start, startLine, startColumn, null);
            case '/' -> token(TokenType.DIVIDE, start, startLine, startColumn, null);
            case '%' -> token(TokenType.MODULO, start, startLine, startColumn, null);
            case ',' -> token(TokenType.COMMA, start, startLine, startColumn, null);
            case ';' -> token(TokenType.SEMICOLON, start, startLine, startColumn, null);
            case '(' -> token(TokenType.LEFT_PAREN, start, startLine, startColumn, null);
            case ')' -> token(TokenType.RIGHT_PAREN, start, startLine, startColumn, null);
            case '.' -> token(TokenType.DOT, start, startLine, startColumn, null);
            case '{' -> throw error("comentário deve começar com '{*'",
                    startLine, startColumn, false);
            default -> throw error("caractere não reconhecido: '" + printable(c) + "'",
                    startLine, startColumn, false);
        };
    }

    private void skipIgnored() {
        boolean repeat;
        do {
            repeat = false;
            while (!isAtEnd() && isWhitespace(peek())) {
                advance();
            }
            if (!isAtEnd() && peek() == '{' && peekNext() == '*') {
                int startLine = line;
                int startColumn = column;
                advance();
                advance();
                while (!isAtEnd() && !(peek() == '*' && peekNext() == '}')) {
                    advance();
                }
                if (isAtEnd()) {
                    throw error("comentário iniciado com '{*' não foi fechado com '*}'",
                            startLine, startColumn, true);
                }
                advance();
                advance();
                repeat = true;
            }
        } while (repeat);
    }

    private Token scanIdentifier(int start, int startLine, int startColumn) {
        while (!isAtEnd() && (isAsciiLetter(peek()) || isAsciiDigit(peek()) || peek() == '_')) {
            advance();
        }

        if (!isAtEnd() && Character.isLetterOrDigit(peek())) {
            consumeUnicodeWordTail();
            String invalid = source.substring(start, index);
            throw error("identificador contém caractere fora de [A-Za-z0-9_]: '"
                    + printable(invalid) + "'", startLine, startColumn, false);
        }

        String lexeme = source.substring(start, index);
        Symbol symbol = symbolTable.find(lexeme);
        if (symbol == null) {
            symbol = symbolTable.installIdentifier(lexeme);
        }
        return new Token(symbol.tokenType(), lexeme, startLine, startColumn,
                "TS[" + symbol.index() + "]");
    }

    private Token scanNumber(int start, int startLine, int startColumn) {
        while (!isAtEnd() && isAsciiDigit(peek())) {
            advance();
        }

        boolean floatingPoint = false;
        if (!isAtEnd() && peek() == '.') {
            floatingPoint = true;
            advance();
            if (isAtEnd() || !isAsciiDigit(peek())) {
                String invalid = source.substring(start, index);
                throw error("constante float inválida: '" + printable(invalid)
                        + "' (é necessário ao menos um dígito após o ponto)",
                        startLine, startColumn, isAtEnd());
            }
            while (!isAtEnd() && isAsciiDigit(peek())) {
                advance();
            }
        }

        if (!isAtEnd() && (isAsciiLetter(peek()) || peek() == '_'
                || Character.isLetterOrDigit(peek()))) {
            consumeUnicodeWordTail();
            String invalid = source.substring(start, index);
            throw error("identificador não pode começar com dígito: '"
                    + printable(invalid) + "'", startLine, startColumn, false);
        }

        String lexeme = source.substring(start, index);
        TokenType type = floatingPoint ? TokenType.FLOAT_CONST : TokenType.INTEGER_CONST;
        return new Token(type, lexeme, startLine, startColumn, lexeme);
    }

    private Token scanChar(int startLine, int startColumn) {
        if (isAtEnd()) {
            throw error("constante char não foi fechada com aspas simples",
                    startLine, startColumn, true);
        }
        if (peek() == '\n' || peek() == '\r') {
            advance();
            throw error("constante char vazia ou quebrada por fim de linha",
                    startLine, startColumn, false);
        }
        if (peek() == '\'') {
            advance();
            throw error("constante char vazia não é permitida",
                    startLine, startColumn, false);
        }

        char value = advance();
        if (!isAscii(value)) {
            consumeUntilCharBoundary();
            throw error("constante char deve conter um caractere ASCII",
                    startLine, startColumn, isAtEnd());
        }
        if (isAtEnd() || peek() != '\'') {
            consumeUntilCharBoundary();
            throw error("constante char deve conter exatamente um caractere e fechar com aspas simples",
                    startLine, startColumn, isAtEnd());
        }
        advance();
        return new Token(TokenType.CHAR_CONST, String.valueOf(value),
                startLine, startColumn, printable(value));
    }

    private Token scanLiteral(int startLine, int startColumn) {
        int contentStart = index;
        boolean invalidAscii = false;
        while (!isAtEnd() && peek() != '"' && peek() != '\n' && peek() != '\r') {
            char c = advance();
            if (!isAscii(c)) {
                invalidAscii = true;
            }
        }

        if (isAtEnd()) {
            throw error("literal não foi fechado com aspas duplas",
                    startLine, startColumn, true);
        }
        if (peek() == '\n' || peek() == '\r') {
            advance();
            throw error("literal não pode conter quebra de linha e não foi fechado",
                    startLine, startColumn, false);
        }

        String value = source.substring(contentStart, index);
        advance();
        if (invalidAscii) {
            throw error("literal contém caractere fora da tabela ASCII: \""
                    + printable(value) + "\"", startLine, startColumn, false);
        }
        return new Token(TokenType.LITERAL, value, startLine, startColumn, value);
    }

    private void consumeUntilCharBoundary() {
        while (!isAtEnd() && peek() != '\'' && peek() != '\n' && peek() != '\r') {
            advance();
        }
        if (!isAtEnd()) {
            advance();
        }
    }

    private void consumeUnicodeWordTail() {
        while (!isAtEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            advance();
        }
    }

    private Token token(TokenType type, int start, int startLine, int startColumn,
                        String attribute) {
        return new Token(type, source.substring(start, index), startLine, startColumn, attribute);
    }

    private boolean match(char expected) {
        if (isAtEnd() || peek() != expected) {
            return false;
        }
        advance();
        return true;
    }

    private char peek() {
        return source.charAt(index);
    }

    private char peekNext() {
        if (index + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(index + 1);
    }

    private char advance() {
        char c = source.charAt(index++);
        if (c == '\r') {
            if (!isAtEnd() && source.charAt(index) == '\n') {
                index++;
            }
            line++;
            column = 1;
            return '\n';
        }
        if (c == '\n') {
            line++;
            column = 1;
        } else if (c == '\t') {
            column += 4;
        } else {
            column++;
        }
        return c;
    }

    private boolean isAtEnd() {
        return index >= source.length();
    }

    private static boolean isWhitespace(char c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f';
    }

    private static boolean isAsciiLetter(char c) {
        return c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z';
    }

    private static boolean isAsciiDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private static boolean isAscii(char c) {
        return c <= 127 && c != '\n' && c != '\r';
    }

    private static String printable(char c) {
        return switch (c) {
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            default -> Character.toString(c);
        };
    }

    private static String printable(String value) {
        return value.replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }

    private static LexicalException error(String message, int line, int column,
                                          boolean atEndOfFile) {
        return new LexicalException(message, line, column, atEndOfFile);
    }
}

