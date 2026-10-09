package br.cefetmg.compilador.symbols;

import br.cefetmg.compilador.lexer.TokenType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

// Tabela de simbolos usando tabela hash.
// O LinkedHashMap guarda a ordem de insercao, assim os indices TS[n] nao mudam.
public class SymbolTable {
    private LinkedHashMap<String, Symbol> entries = new LinkedHashMap<>();

    public SymbolTable() {
        // palavras reservadas entram na tabela antes de comecar a analise
        addReserved("program", TokenType.PROGRAM);
        addReserved("begin", TokenType.BEGIN);
        addReserved("end", TokenType.END);
        addReserved("int", TokenType.INT);
        addReserved("float", TokenType.FLOAT);
        addReserved("char", TokenType.CHAR);
        addReserved("if", TokenType.IF);
        addReserved("then", TokenType.THEN);
        addReserved("else", TokenType.ELSE);
        addReserved("repeat", TokenType.REPEAT);
        addReserved("until", TokenType.UNTIL);
        addReserved("while", TokenType.WHILE);
        addReserved("do", TokenType.DO);
        addReserved("read", TokenType.READ);
        addReserved("write", TokenType.WRITE);
    }

    private void addReserved(String lexeme, TokenType type) {
        int index = entries.size() + 1;
        entries.put(lexeme, new Symbol(index, lexeme, Symbol.PALAVRA_RESERVADA, type));
    }

    public Symbol addIdentifier(String lexeme) {
        Symbol symbol = entries.get(lexeme);
        if (symbol == null) {
            int index = entries.size() + 1;
            symbol = new Symbol(index, lexeme, Symbol.IDENTIFICADOR, TokenType.IDENTIFIER);
            entries.put(lexeme, symbol);
        }
        return symbol;
    }

    public Symbol find(String lexeme) {
        return entries.get(lexeme);
    }

    public List<Symbol> getSymbols() {
        return new ArrayList<>(entries.values());
    }
}
