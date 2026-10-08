package br.cefetmg.compilador.lexer;

/** Tipos de token reconhecidos pelo analisador léxico. */
public enum TokenType {
    // Palavras reservadas
    PROGRAM,
    BEGIN,
    END,
    INT,
    FLOAT,
    CHAR,
    IF,
    THEN,
    ELSE,
    REPEAT,
    UNTIL,
    WHILE,
    DO,
    READ,
    WRITE,

    // Identificadores e constantes
    IDENTIFIER,
    INTEGER_CONST,
    FLOAT_CONST,
    CHAR_CONST,
    LITERAL,

    // Operadores
    ASSIGN,          // =
    EQUAL,           // ==
    GREATER,         // >
    GREATER_EQUAL,   // >=
    LESS,            // <
    LESS_EQUAL,      // <=
    NOT_EQUAL,       // !=
    PLUS,            // +
    MINUS,           // -
    OR,              // ||
    MULTIPLY,        // *
    DIVIDE,          // /
    MODULO,          // %
    AND,             // &&
    NOT,             // !

    // Pontuação
    COMMA,
    SEMICOLON,
    LEFT_PAREN,
    RIGHT_PAREN,
    DOT,

    EOF
}

