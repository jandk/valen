package be.twofold.valen.game.idtech.lexer;

import org.junit.jupiter.api.*;

import static be.twofold.valen.game.idtech.lexer.LexerFlags.*;
import static be.twofold.valen.game.idtech.lexer.TokenType.*;
import static org.assertj.core.api.Assertions.*;

class IdLexerNameTest {
    @Test
    void testReadsSimpleName() {
        var token = token("foo");
        assertThat(token.type()).isEqualTo(TT_NAME);
        assertThat(token.value()).isEqualTo("foo");
    }

    @Test
    void testSubtypeIsLength() {
        assertThat(token("foo").subtype()).isEqualTo(3);
        assertThat(token("f").subtype()).isEqualTo(1);
    }

    @Test
    void testReadsLettersDigitsAndUnderscores() {
        assertThat(name("_foo_Bar_1")).isEqualTo("_foo_Bar_1");
    }

    @Test
    void testReportsPositionOfTokenStart() {
        var lexer = lexer("\n  foo");
        lexer.readWhiteSpace();
        var token = lexer.readName();
        assertThat(token.line()).isEqualTo(2);
        assertThat(token.column()).isEqualTo(3);
    }

    @Test
    void testStopsAtWhitespace() {
        assertThat(name("foo bar")).isEqualTo("foo");
    }

    @Test
    void testStopsAtPunctuation() {
        assertThat(name("foo(")).isEqualTo("foo");
    }

    @Test
    void testLeavesTerminatorUnconsumed() {
        var lexer = lexer("foo(");
        assertThat(lexer.readName().value()).isEqualTo("foo");
        assertThat(lexer.readPunctuation().subtype()).isEqualTo(LexerPunctuation.P_PARENTHESESOPEN.ordinal());
    }

    @Test
    void testReadsConsecutiveNames() {
        var lexer = lexer("foo bar");
        assertThat(lexer.readName().value()).isEqualTo("foo");
        lexer.readWhiteSpace();
        assertThat(lexer.readName().value()).isEqualTo("bar");
    }

    @Test
    void testReadsNameAtEndOfSource() {
        var lexer = lexer("foo");
        assertThat(lexer.readName().value()).isEqualTo("foo");
        assertThat(lexer.readWhiteSpace()).isFalse();
    }

    @Test
    void testStopsAtDashByDefault() {
        assertThat(name("foo-bar")).isEqualTo("foo");
    }

    @Test
    void testOnlyStringsIncludesDash() {
        assertThat(name("foo-bar", LEXFL_ONLYSTRINGS)).isEqualTo("foo-bar");
    }

    @Test
    void testStopsAtPathCharactersByDefault() {
        assertThat(name("foo/bar")).isEqualTo("foo");
        assertThat(name("foo\\bar")).isEqualTo("foo");
        assertThat(name("foo:bar")).isEqualTo("foo");
        assertThat(name("foo.bar")).isEqualTo("foo");
        assertThat(name("foo$bar")).isEqualTo("foo");
    }

    @Test
    void testAllowPathNamesIncludesPathCharacters() {
        assertThat(name("foo/bar", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo/bar");
        assertThat(name("foo\\bar", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo\\bar");
        assertThat(name("foo:bar", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo:bar");
        assertThat(name("foo.bar", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo.bar");
    }

    @Test
    void testAllowPathNamesIncludesDollar() {
        assertThat(name("foo$bar", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo$bar");
    }

    @Test
    void testAllowPathNamesReadsWholePath() {
        assertThat(name("art/models/foo.md6", LEXFL_ALLOWPATHNAMES)).isEqualTo("art/models/foo.md6");
    }

    @Test
    void testAllowPathNamesStopsAtAt() {
        assertThat(name("foo@bar", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo");
    }

    @Test
    void testAllowPathNamesStopsAtDash() {
        assertThat(name("foo-bar", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo");
    }

    @Test
    void testStopsAtWildcardByDefault() {
        assertThat(name("foo*", LEXFL_ALLOWPATHNAMES)).isEqualTo("foo");
    }

    @Test
    void testAllowWildcardIncludesWildcard() {
        assertThat(name("foo*", LEXFL_ALLOWWILDCARD)).isEqualTo("foo*");
        assertThat(name("*foo*bar", LEXFL_ALLOWWILDCARD)).isEqualTo("*foo*bar");
    }

    @Test
    void testAllowWildcardStopsAtPathCharacters() {
        assertThat(name("foo/bar", LEXFL_ALLOWWILDCARD)).isEqualTo("foo");
    }

    @Test
    void testAllowPathNamesAndWildcardCombine() {
        assertThat(name("art/*.md6", LEXFL_ALLOWPATHNAMES, LEXFL_ALLOWWILDCARD)).isEqualTo("art/*.md6");
    }

    @Test
    void testLeavesFollowingTokenReadable() {
        var lexer = lexer("foo\"x\"");
        assertThat(lexer.readName().value()).isEqualTo("foo");
        assertThat(lexer.readString('"').value()).isEqualTo("x");
    }

    private IdLexer lexer(String source, LexerFlags... flags) {
        return new IdLexer(source, flags);
    }

    private IdToken token(String source, LexerFlags... flags) {
        return lexer(source, flags).readName();
    }

    private String name(String source, LexerFlags... flags) {
        var token = token(source, flags);
        return token == null ? null : token.value();
    }
}
