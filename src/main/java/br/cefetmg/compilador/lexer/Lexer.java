package br.cefetmg.compilador.lexer;

import br.cefetmg.compilador.symbols.Symbol;
import br.cefetmg.compilador.symbols.SymbolTable;

// Analisador lexico feito a mao, seguindo os automatos de cada token.
// Le o codigo fonte caractere por caractere e devolve um token por chamada de nextToken().
public class Lexer {
    private String source;
    private SymbolTable symbolTable;
    private int pos = 0;     // posicao atual no texto
    private int line = 1;
    private int column = 1;

    public Lexer(String source, SymbolTable symbolTable) {
        this.source = source;
        this.symbolTable = symbolTable;
    }

    public Token nextToken() {
        skipSpacesAndComments();

        if (isEnd()) {
            return new Token(TokenType.EOF, "", line, column, null);
        }

        // guarda onde o token comeca, para o lexema e para as mensagens de erro
        int start = pos;
        int startLine = line;
        int startColumn = column;
        char c = next();

        if (isLetter(c) || c == '_') {
            return readIdentifier(start, startLine, startColumn);
        }
        if (isDigit(c)) {
            return readNumber(start, startLine, startColumn);
        }
        if (Character.isLetterOrDigit(c)) {
            // letra acentuada ou outro caractere fora do ASCII
            skipWord();
            String word = source.substring(start, pos);
            throw new LexicalException("identificador contém caractere fora de [A-Za-z0-9_]: '"
                    + word + "'", startLine, startColumn, false);
        }
        if (c == '\'') {
            return readChar(startLine, startColumn);
        }
        if (c == '"') {
            return readLiteral(startLine, startColumn);
        }

        // operadores de dois caracteres: olha o proximo para decidir
        if (c == '=') {
            if (nextIs('=')) {
                return makeToken(TokenType.EQUAL, start, startLine, startColumn);
            }
            return makeToken(TokenType.ASSIGN, start, startLine, startColumn);
        }
        if (c == '>') {
            if (nextIs('=')) {
                return makeToken(TokenType.GREATER_EQUAL, start, startLine, startColumn);
            }
            return makeToken(TokenType.GREATER, start, startLine, startColumn);
        }
        if (c == '<') {
            if (nextIs('=')) {
                return makeToken(TokenType.LESS_EQUAL, start, startLine, startColumn);
            }
            return makeToken(TokenType.LESS, start, startLine, startColumn);
        }
        if (c == '!') {
            if (nextIs('=')) {
                return makeToken(TokenType.NOT_EQUAL, start, startLine, startColumn);
            }
            return makeToken(TokenType.NOT, start, startLine, startColumn);
        }
        if (c == '|') {
            if (nextIs('|')) {
                return makeToken(TokenType.OR, start, startLine, startColumn);
            }
            throw new LexicalException("operador '|' incompleto; use '||'", startLine, startColumn, false);
        }
        if (c == '&') {
            if (nextIs('&')) {
                return makeToken(TokenType.AND, start, startLine, startColumn);
            }
            throw new LexicalException("operador '&' incompleto; use '&&'", startLine, startColumn, false);
        }

        // operadores e pontuacao de um caractere
        if (c == '+') {
            return makeToken(TokenType.PLUS, start, startLine, startColumn);
        } else if (c == '-') {
            return makeToken(TokenType.MINUS, start, startLine, startColumn);
        } else if (c == '*') {
            return makeToken(TokenType.MULTIPLY, start, startLine, startColumn);
        } else if (c == '/') {
            return makeToken(TokenType.DIVIDE, start, startLine, startColumn);
        } else if (c == '%') {
            return makeToken(TokenType.MODULO, start, startLine, startColumn);
        } else if (c == ',') {
            return makeToken(TokenType.COMMA, start, startLine, startColumn);
        } else if (c == ';') {
            return makeToken(TokenType.SEMICOLON, start, startLine, startColumn);
        } else if (c == '(') {
            return makeToken(TokenType.LEFT_PAREN, start, startLine, startColumn);
        } else if (c == ')') {
            return makeToken(TokenType.RIGHT_PAREN, start, startLine, startColumn);
        } else if (c == '.') {
            return makeToken(TokenType.DOT, start, startLine, startColumn);
        } else if (c == '{') {
            throw new LexicalException("comentário deve começar com '{*'", startLine, startColumn, false);
        }

        throw new LexicalException("caractere não reconhecido: '" + c + "'", startLine, startColumn, false);
    }

    // pula espacos, quebras de linha e comentarios {* ... *}
    private void skipSpacesAndComments() {
        while (!isEnd()) {
            char c = peek();
            if (c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '\f') {
                next();
            } else if (c == '{' && peekNext() == '*') {
                int startLine = line;
                int startColumn = column;
                next(); // {
                next(); // *
                while (!isEnd() && !(peek() == '*' && peekNext() == '}')) {
                    next();
                }
                if (isEnd()) {
                    throw new LexicalException("comentário iniciado com '{*' não foi fechado com '*}'",
                            startLine, startColumn, true);
                }
                next(); // *
                next(); // }
            } else {
                break;
            }
        }
    }

    private Token readIdentifier(int start, int startLine, int startColumn) {
        while (!isEnd() && (isLetter(peek()) || isDigit(peek()) || peek() == '_')) {
            next();
        }

        // ex.: pontuação -> parou no 'ç', que nao e ASCII
        if (!isEnd() && Character.isLetterOrDigit(peek())) {
            skipWord();
            String word = source.substring(start, pos);
            throw new LexicalException("identificador contém caractere fora de [A-Za-z0-9_]: '"
                    + word + "'", startLine, startColumn, false);
        }

        String lexeme = source.substring(start, pos);
        Symbol symbol = symbolTable.find(lexeme);
        if (symbol == null) {
            symbol = symbolTable.addIdentifier(lexeme);
        }
        return new Token(symbol.getTokenType(), lexeme, startLine, startColumn,
                "TS[" + symbol.getIndex() + "]");
    }

    // integer_const ::= digit+    float_const ::= digit+ "." digit+
    private Token readNumber(int start, int startLine, int startColumn) {
        while (!isEnd() && isDigit(peek())) {
            next();
        }

        boolean isFloat = false;
        if (!isEnd() && peek() == '.') {
            isFloat = true;
            next();
            if (isEnd() || !isDigit(peek())) {
                String number = source.substring(start, pos);
                throw new LexicalException("constante float inválida: '" + number
                        + "' (é necessário ao menos um dígito após o ponto)",
                        startLine, startColumn, isEnd());
            }
            while (!isEnd() && isDigit(peek())) {
                next();
            }
        }

        // ex.: 1a, 2base -> identificador comecando com digito
        if (!isEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            skipWord();
            String word = source.substring(start, pos);
            throw new LexicalException("identificador não pode começar com dígito: '" + word + "'",
                    startLine, startColumn, false);
        }

        String lexeme = source.substring(start, pos);
        if (isFloat) {
            return new Token(TokenType.FLOAT_CONST, lexeme, startLine, startColumn, lexeme);
        }
        return new Token(TokenType.INTEGER_CONST, lexeme, startLine, startColumn, lexeme);
    }

    // char_const ::= ' carac '   (a primeira aspa ja foi lida)
    private Token readChar(int startLine, int startColumn) {
        if (isEnd()) {
            throw new LexicalException("constante char não foi fechada com aspas simples",
                    startLine, startColumn, true);
        }
        if (peek() == '\n' || peek() == '\r') {
            next();
            throw new LexicalException("constante char vazia ou quebrada por fim de linha",
                    startLine, startColumn, false);
        }
        if (peek() == '\'') {
            next();
            throw new LexicalException("constante char vazia não é permitida",
                    startLine, startColumn, false);
        }

        char value = next();
        if (value > 127) {
            skipUntilQuote();
            throw new LexicalException("constante char deve conter um caractere ASCII",
                    startLine, startColumn, isEnd());
        }
        if (isEnd() || peek() != '\'') {
            skipUntilQuote();
            throw new LexicalException("constante char deve conter exatamente um caractere e fechar com aspas simples",
                    startLine, startColumn, isEnd());
        }
        next(); // aspa de fechamento
        String text = String.valueOf(value);
        if (value == '\t') {
            text = "\\t";
        }
        return new Token(TokenType.CHAR_CONST, String.valueOf(value), startLine, startColumn, text);
    }

    // literal ::= " caractere* "   (a primeira aspa ja foi lida)
    private Token readLiteral(int startLine, int startColumn) {
        int contentStart = pos;
        boolean hasNonAscii = false;
        while (!isEnd() && peek() != '"' && peek() != '\n' && peek() != '\r') {
            char c = next();
            if (c > 127) {
                hasNonAscii = true;
            }
        }

        if (isEnd()) {
            throw new LexicalException("literal não foi fechado com aspas duplas",
                    startLine, startColumn, true);
        }
        if (peek() == '\n' || peek() == '\r') {
            next();
            throw new LexicalException("literal não pode conter quebra de linha e não foi fechado",
                    startLine, startColumn, false);
        }

        String value = source.substring(contentStart, pos);
        next(); // aspa de fechamento
        if (hasNonAscii) {
            throw new LexicalException("literal contém caractere fora da tabela ASCII: \"" + value + "\"",
                    startLine, startColumn, false);
        }
        return new Token(TokenType.LITERAL, value, startLine, startColumn, value);
    }

    // usado para descartar o resto de uma constante char com erro
    private void skipUntilQuote() {
        while (!isEnd() && peek() != '\'' && peek() != '\n' && peek() != '\r') {
            next();
        }
        if (!isEnd()) {
            next();
        }
    }

    // usado para descartar o resto de uma palavra com erro
    private void skipWord() {
        while (!isEnd() && (Character.isLetterOrDigit(peek()) || peek() == '_')) {
            next();
        }
    }

    private Token makeToken(TokenType type, int start, int startLine, int startColumn) {
        return new Token(type, source.substring(start, pos), startLine, startColumn, null);
    }

    // se o proximo caractere for o esperado, consome ele e retorna true
    private boolean nextIs(char expected) {
        if (isEnd() || peek() != expected) {
            return false;
        }
        next();
        return true;
    }

    private char peek() {
        return source.charAt(pos);
    }

    private char peekNext() {
        if (pos + 1 >= source.length()) {
            return '\0';
        }
        return source.charAt(pos + 1);
    }

    // consome um caractere e atualiza linha e coluna
    private char next() {
        char c = source.charAt(pos);
        pos++;
        if (c == '\r') {
            // \r\n (Windows) conta como uma quebra de linha so
            if (!isEnd() && source.charAt(pos) == '\n') {
                pos++;
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

    private boolean isEnd() {
        return pos >= source.length();
    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }
}
