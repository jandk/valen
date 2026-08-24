package be.twofold.valen.game.idtech.lexer;

import org.junit.jupiter.api.*;

import java.util.*;

import static be.twofold.valen.game.idtech.lexer.LexerFlags.*;
import static be.twofold.valen.game.idtech.lexer.TokenType.*;
import static org.assertj.core.api.Assertions.*;

class IdLexerTokenTest {
    private static final int TT_RAWBLOCK = 0x40000;

    private static IdLexer lexer(String source, LexerFlags... flags) {
        return new IdLexer(source, flags);
    }

    private static IdToken token(String source, LexerFlags... flags) {
        return lexer(source, flags).readToken();
    }

    private static List<IdToken> tokens(String source, LexerFlags... flags) {
        var lexer = lexer(source, flags);
        var result = new ArrayList<IdToken>();
        for (IdToken token = lexer.readToken(); token != null; token = lexer.readToken()) {
            result.add(token);
        }
        return result;
    }

    @Test
    void testReadsEachTokenType() {
        var tokens = tokens("foo 123 \"bar\" +");
        assertThat(tokens).map(IdToken::type)
            .containsExactly(TT_NAME, TT_NUMBER, TT_STRING, TT_PUNCTUATION);
        assertThat(tokens).map(IdToken::value)
            .containsExactly("foo", "123", "bar", "+");
    }

    @Test
    void testReturnsNullAtEndOfInput() {
        assertThat(token("")).isNull();
    }

    @Test
    void testReturnsNullAfterTrailingWhitespace() {
        assertThat(tokens("foo  \n  ")).map(IdToken::value).containsExactly("foo");
    }

    @Test
    void testReturnsNullForCommentOnly() {
        assertThat(tokens("// nothing\n/* nor this */")).isEmpty();
    }

    @Test
    void testSkipsCommentsBetweenTokens() {
        assertThat(tokens("a /* x */ b // y\nc")).map(IdToken::value)
            .containsExactly("a", "b", "c");
    }

    @Test
    void testReportsPositionAcrossLines() {
        var tokens = tokens("foo\n  bar");
        assertThat(tokens.get(0).line()).isEqualTo(1);
        assertThat(tokens.get(0).column()).isEqualTo(1);
        assertThat(tokens.get(1).line()).isEqualTo(2);
        assertThat(tokens.get(1).column()).isEqualTo(3);
    }

    @Test
    void testNumberIsNotGluedToFollowingName() {
        assertThat(tokens("123abc")).map(IdToken::value).containsExactly("123", "abc");
    }

    @Test
    void testLeadingDotIsNumberWhenFollowedByDigit() {
        var token = token(".5");
        assertThat(token.type()).isEqualTo(TT_NUMBER);
        assertThat(token.value()).isEqualTo(".5");
    }

    @Test
    void testLeadingDotIsPunctuationByDefault() {
        var token = token(".foo");
        assertThat(token.type()).isEqualTo(TT_PUNCTUATION);
        assertThat(token.subtype()).isEqualTo(LexerPunctuation.P_MEMBER_SELECTION_OBJECT.ordinal());
    }

    @Test
    void testLeadingDotIsNameWithPathNames() {
        var token = token(".foo", LEXFL_ALLOWPATHNAMES);
        assertThat(token.type()).isEqualTo(TT_NAME);
        assertThat(token.value()).isEqualTo(".foo");
    }

    @Test
    void testWildcardIsPunctuationByDefault() {
        var token = token("*foo");
        assertThat(token.type()).isEqualTo(TT_PUNCTUATION);
        assertThat(token.subtype()).isEqualTo(LexerPunctuation.P_MUL.ordinal());
    }

    @Test
    void testWildcardIsNameWithWildcardFlag() {
        var token = token("*foo", LEXFL_ALLOWWILDCARD);
        assertThat(token.type()).isEqualTo(TT_NAME);
        assertThat(token.value()).isEqualTo("*foo");
    }

    @Test
    void testQuoteIsPunctuationWithNoStrings() {
        var token = token("\"foo\"", LEXFL_NOSTRINGS);
        assertThat(token.type()).isEqualTo(TT_PUNCTUATION);
        assertThat(token.subtype()).isEqualTo(LexerPunctuation.P_QUOTE.ordinal());
    }

    @Test
    void testOnlyStringsReadsBareWordsAsNames() {
        assertThat(tokens("foo-bar baz", LEXFL_ONLYSTRINGS)).map(IdToken::value)
            .containsExactly("foo-bar", "baz");
    }

    @Test
    void testOnlyStringsReadsNumbersAsNames() {
        var token = token("123", LEXFL_ONLYSTRINGS);
        assertThat(token.type()).isEqualTo(TT_NAME);
        assertThat(token.value()).isEqualTo("123");
    }

    @Test
    void testOnlyStringsStillReadsQuotedStrings() {
        var token = token("\"a b\"", LEXFL_ONLYSTRINGS);
        assertThat(token.type()).isEqualTo(TT_STRING);
        assertThat(token.value()).isEqualTo("a b");
    }

    @Test
    void testOnlyStringsIgnoresNoStrings() {
        var token = token("\"a b\"", LEXFL_ONLYSTRINGS, LEXFL_NOSTRINGS);
        assertThat(token.type()).isEqualTo(TT_STRING);
        assertThat(token.value()).isEqualTo("a b");
    }

    @Test
    void testRawBlockNeedsItsFlag() {
        var token = token("<%a%>");
        assertThat(token.type()).isEqualTo(TT_PUNCTUATION);
        assertThat(token.subtype()).isEqualTo(LexerPunctuation.P_LOGIC_LESS.ordinal());
    }

    @Test
    void testReadsRawBlock() {
        var token = token("<%hello%>", LEXFL_ALLOWRAWSTRINGBLOCKS);
        assertThat(token.type()).isEqualTo(TT_STRING);
        assertThat(token.subtype()).isEqualTo(TT_RAWBLOCK);
        assertThat(token.value()).isEqualTo("hello");
    }

    @Test
    void testRawBlockKeepsNewlinesAndQuotes() {
        var token = token("<%a\n\"b\"%>", LEXFL_ALLOWRAWSTRINGBLOCKS);
        assertThat(token.value()).isEqualTo("a\n\"b\"");
    }

    @Test
    void testRawBlockKeepsLonePercent() {
        var token = token("<%50% off%>", LEXFL_ALLOWRAWSTRINGBLOCKS);
        assertThat(token.value()).isEqualTo("50% off");
    }

    @Test
    void testRawBlockLeavesFollowingTokenReadable() {
        assertThat(tokens("<%a%> b", LEXFL_ALLOWRAWSTRINGBLOCKS)).map(IdToken::value)
            .containsExactly("a", "b");
    }

    @Test
    @Timeout(2)
    void testRejectsUnterminatedRawBlock() {
        assertThatThrownBy(() -> token("<%a", LEXFL_ALLOWRAWSTRINGBLOCKS))
            .isInstanceOf(LexerException.class)
            .hasMessageContaining("missing trailing identifier");
    }
}
