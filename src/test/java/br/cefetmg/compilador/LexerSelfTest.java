package br.cefetmg.compilador;

import br.cefetmg.compilador.lexer.Token;
import br.cefetmg.compilador.lexer.TokenType;
import br.cefetmg.compilador.symbols.Symbol;

import java.util.List;

/** Testes sem bibliotecas externas, executáveis com java -ea. */
public final class LexerSelfTest {
    private static int executed;

    private LexerSelfTest() {
    }

    public static void main(String[] args) {
        testAllTokenFamilies();
        testCommentsAndLocations();
        testCommentWithoutAsterisk();
        testReservedWordsAndSymbolDeduplication();
        testMalformedNumericIdentifier();
        testMalformedFloat();
        testUnclosedString();
        testUnclosedComment();
        testInvalidOperators();
        testColonIsNotInTheLanguage();
        testAsciiRestriction();
        System.out.println("OK - " + executed + " testes automatizados passaram.");
    }

    private static void testAllTokenFamilies() {
        String source = "program p begin a=10; b=2.5; c='X'; write(\"ok\"); "
                + "if a>=1 && a!=2 || !a==3 then a=a+1-2*3/4%2 end end.";
        AnalysisResult result = analyze(source, false);
        assertTrue(result.successful(), "famílias de tokens deveriam ser válidas");
        List<TokenType> types = result.tokens().stream().map(Token::type).toList();
        assertTrue(types.containsAll(List.of(TokenType.INTEGER_CONST, TokenType.FLOAT_CONST,
                TokenType.CHAR_CONST, TokenType.LITERAL, TokenType.GREATER_EQUAL,
                TokenType.AND, TokenType.NOT_EQUAL, TokenType.OR, TokenType.NOT,
                TokenType.EQUAL, TokenType.PLUS, TokenType.MINUS, TokenType.MULTIPLY,
                TokenType.DIVIDE, TokenType.MODULO, TokenType.DOT)),
                "faltou reconhecer alguma família de tokens");
    }

    private static void testCommentsAndLocations() {
        AnalysisResult result = analyze("{* comentário } com *\n multilinha *}\nprogram p", false);
        assertTrue(result.successful(), "comentário fechado deveria ser ignorado");
        Token program = result.tokens().get(0);
        assertEquals(3, program.line(), "linha após comentário");
        assertEquals(1, program.column(), "coluna após comentário");
    }

    private static void testCommentWithoutAsterisk() {
        assertError("{ comentário }", "comentário deve começar com '{*'");
    }

    private static void testReservedWordsAndSymbolDeduplication() {
        AnalysisResult result = analyze("program program nome nome Nome _var", false);
        long identifiers = result.symbolTable().symbols().stream()
                .filter(s -> s.category() == Symbol.Category.IDENTIFICADOR)
                .count();
        assertEquals(3L, identifiers, "identificadores únicos e case-sensitive");
        assertEquals(TokenType.PROGRAM, result.tokens().get(0).type(), "palavra reservada");
        assertEquals(TokenType.IDENTIFIER, result.tokens().get(2).type(), "identificador");
    }

    private static void testMalformedNumericIdentifier() {
        assertError("1abc", "não pode começar com dígito");
    }

    private static void testMalformedFloat() {
        assertError("34.", "constante float inválida");
    }

    private static void testUnclosedString() {
        assertError("\"texto\nprogram", "literal não pode conter quebra de linha");
    }

    private static void testUnclosedComment() {
        assertError("{* comentário }", "comentário iniciado");
    }

    private static void testInvalidOperators() {
        AnalysisResult result = analyze("| &", true);
        assertEquals(2, result.errors().size(), "operadores incompletos");
    }

    private static void testColonIsNotInTheLanguage() {
        assertError("a : int", "caractere não reconhecido: ':'");
    }

    private static void testAsciiRestriction() {
        AnalysisResult result = analyze("pontuação \"Perímetro\"", true);
        assertEquals(2, result.errors().size(), "restrição ASCII");
    }

    private static void assertError(String source, String expectedMessagePart) {
        AnalysisResult result = analyze(source, false);
        assertTrue(!result.successful(), "era esperado erro para: " + source);
        assertTrue(result.errors().get(0).getMessage().contains(expectedMessagePart),
                "mensagem inesperada: " + result.errors().get(0).getMessage());
    }

    private static AnalysisResult analyze(String source, boolean allErrors) {
        executed++;
        return new LexicalAnalyzer().analyze(source, allErrors);
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": esperado=" + expected + ", obtido=" + actual);
        }
    }
}

