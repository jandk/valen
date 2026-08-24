package be.twofold.valen.game.idtech.lexer;

import org.junit.jupiter.api.*;

import static be.twofold.valen.game.idtech.lexer.LexerFlags.*;
import static be.twofold.valen.game.idtech.lexer.TokenType.*;
import static org.assertj.core.api.Assertions.*;

class IdLexerStringTest {
    private static IdLexer lexer(String source, LexerFlags... flags) {
        return new IdLexer(source, flags);
    }

    private static IdToken token(String source, LexerFlags... flags) {
        return lexer(source, flags).readString('"');
    }

    private static String string(String source, LexerFlags... flags) {
        return token(source, flags).value();
    }

    @Test
    void testReadsSimpleString() {
        assertThat(string("\"hello\"")).isEqualTo("hello");
    }

    @Test
    void testReadsEmptyString() {
        assertThat(string("\"\"")).isEqualTo("");
    }

    @Test
    void testReadsStringWithSpaces() {
        assertThat(string("\"hello world\"")).isEqualTo("hello world");
    }

    @Test
    void testReadsLiteral() {
        assertThat(lexer("'a'").readString('\'').value()).isEqualTo("a");
    }

    @Test
    void testStringHasStringTypeAndZeroSubtype() {
        var token = token("\"hello\"");
        assertThat(token.type()).isEqualTo(TT_STRING);
        assertThat(token.subtype()).isEqualTo(0);
    }

    @Test
    void testLiteralSubtypeIsFirstCharacter() {
        var token = lexer("'a'").readString('\'');
        assertThat(token.type()).isEqualTo(TT_LITERAL);
        assertThat(token.subtype()).isEqualTo('a');
    }

    @Test
    void testMultiCharacterLiteralSubtypeIsFirstCharacter() {
        var token = lexer("'ab'", LEXFL_ALLOWMULTICHARLITERALS).readString('\'');
        assertThat(token.value()).isEqualTo("ab");
        assertThat(token.subtype()).isEqualTo('a');
    }

    @Test
    void testEmptyLiteralSubtypeIsZero() {
        var token = lexer("''").readString('\'');
        assertThat(token.value()).isEqualTo("");
        assertThat(token.subtype()).isEqualTo(0);
    }

    @Test
    void testReportsPositionOfTokenStart() {
        var lexer = lexer("\n  \"abc\"");
        lexer.readWhiteSpace();
        var token = lexer.readString('"');
        assertThat(token.line()).isEqualTo(2);
        assertThat(token.column()).isEqualTo(3);
    }

    @Test
    void testInterpretsNewlineEscape() {
        assertThat(string("\"a\\nb\"")).isEqualTo("a\nb");
    }

    @Test
    void testInterpretsTabEscape() {
        assertThat(string("\"a\\tb\"")).isEqualTo("a\tb");
    }

    @Test
    void testInterpretsBackslashEscape() {
        assertThat(string("\"a\\\\b\"")).isEqualTo("a\\b");
    }

    @Test
    void testEscapedQuoteDoesNotTerminate() {
        assertThat(string("\"a\\\"b\"")).isEqualTo("a\"b");
    }

    @Test
    void testInterpretsHexEscape() {
        assertThat(string("\"\\x41\"")).isEqualTo("A");
    }

    @Test
    void testInterpretsDecimalEscape() {
        assertThat(string("\"\\65\"")).isEqualTo("A");
    }

    @Test
    void testHexEscapeAcceptsWholeAlphabet() {
        assertThat(string("\"\\xz\"")).isEqualTo("#");
    }

    @Test
    void testNoStringEscapeCharsKeepsBackslash() {
        assertThat(string("\"a\\nb\"", LEXFL_NOSTRINGESCAPECHARS)).isEqualTo("a\\nb");
    }

    @Test
    void testNoStringEscapeCharsLetsEscapedQuoteTerminate() {
        assertThat(string("\"a\\\"", LEXFL_NOSTRINGESCAPECHARS)).isEqualTo("a\\");
    }

    @Test
    void testNoEmitStringEscapeCharsKeepsSequence() {
        assertThat(string("\"a\\nb\"", LEXFL_NOEMITSTRINGESCAPECHARS)).isEqualTo("a\\nb");
    }

    @Test
    void testNoEmitStringEscapeCharsConsumesEscapedQuote() {
        assertThat(string("\"a\\\"b\"", LEXFL_NOEMITSTRINGESCAPECHARS)).isEqualTo("a\\\"b");
    }

    @Test
    void testConcatenatesAdjacentStrings() {
        assertThat(string("\"foo\" \"bar\"")).isEqualTo("foobar");
    }

    @Test
    void testConcatenatesAcrossNewline() {
        assertThat(string("\"foo\"\n\"bar\"")).isEqualTo("foobar");
    }

    @Test
    void testConcatenatesAcrossComment() {
        assertThat(string("\"foo\" /* x */ \"bar\"")).isEqualTo("foobar");
    }

    @Test
    void testConcatenatesThreeStrings() {
        assertThat(string("\"a\" \"b\" \"c\"")).isEqualTo("abc");
    }

    @Test
    void testDoesNotConcatenateWithNoStringConcat() {
        assertThat(string("\"foo\" \"bar\"", LEXFL_NOSTRINGCONCAT)).isEqualTo("foo");
    }

    @Test
    void testDoesNotConcatenateWithNonString() {
        assertThat(string("\"foo\" 123")).isEqualTo("foo");
    }

    @Test
    void testRejectsUnterminatedString() {
        assertThatThrownBy(() -> string("\"foo"))
            .isInstanceOf(LexerException.class)
            .hasMessageContaining("missing trailing quote");
    }

    @Test
    void testRejectsNewlineInsideString() {
        assertThatThrownBy(() -> string("\"foo\nbar\""))
            .isInstanceOf(LexerException.class)
            .hasMessageContaining("newline inside string");
    }

    @Test
    void testReadsConsecutiveStrings() {
        var lexer = lexer("\"a\"\"b\"", LEXFL_NOSTRINGCONCAT);
        assertThat(lexer.readString('"').value()).isEqualTo("a");
        assertThat(lexer.readString('"').value()).isEqualTo("b");
    }

    @Test
    void testLeavesFollowingTokenReadable() {
        var lexer = lexer("\"abc\"123");
        assertThat(lexer.readString('"').value()).isEqualTo("abc");
        assertThat(lexer.readNumber().value()).isEqualTo("123");
    }

    @Test
    void testRejectsUnknownEscapeCharacter() {
        assertThatThrownBy(() -> string("\"a\\qb\""))
            .isInstanceOf(LexerException.class)
            .hasMessageContaining("unknown escape char");
    }

    @Test
    void testErrorMessageCarriesNameAndPosition() {
        var lexer = new IdLexer("test.decl", "\n\"foo");
        lexer.readWhiteSpace();
        assertThatThrownBy(() -> lexer.readString('"'))
            .isInstanceOf(LexerException.class)
            .hasMessageContaining("test.decl (2:");
    }
}
