package br.cefetmg.compilador.symbols;

import br.cefetmg.compilador.lexer.TokenType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tabela hash que preserva a ordem de instalação para produzir índices
 * determinísticos na saída do compilador.
 */
public final class SymbolTable {
    private final Map<String, Symbol> entries = new LinkedHashMap<>();

    public SymbolTable() {
        installReserved("program", TokenType.PROGRAM);
        installReserved("begin", TokenType.BEGIN);
        installReserved("end", TokenType.END);
        installReserved("int", TokenType.INT);
        installReserved("float", TokenType.FLOAT);
        installReserved("char", TokenType.CHAR);
        installReserved("if", TokenType.IF);
        installReserved("then", TokenType.THEN);
        installReserved("else", TokenType.ELSE);
        installReserved("repeat", TokenType.REPEAT);
        installReserved("until", TokenType.UNTIL);
        installReserved("while", TokenType.WHILE);
        installReserved("do", TokenType.DO);
        installReserved("read", TokenType.READ);
        installReserved("write", TokenType.WRITE);
    }

    private void installReserved(String lexeme, TokenType tokenType) {
        Symbol symbol = new Symbol(entries.size() + 1, lexeme,
                Symbol.Category.PALAVRA_RESERVADA, tokenType);
        entries.put(lexeme, symbol);
    }

    public Symbol installIdentifier(String lexeme) {
        return entries.computeIfAbsent(lexeme,
                key -> new Symbol(entries.size() + 1, key,
                        Symbol.Category.IDENTIFICADOR, TokenType.IDENTIFIER));
    }

    public Symbol find(String lexeme) {
        return entries.get(lexeme);
    }

    public List<Symbol> symbols() {
        return Collections.unmodifiableList(new ArrayList<>(entries.values()));
    }
}

