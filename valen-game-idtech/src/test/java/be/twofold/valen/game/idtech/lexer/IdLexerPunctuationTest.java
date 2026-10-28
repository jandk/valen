package be.twofold.valen.game.idtech.lexer;

import org.junit.jupiter.api.*;

import java.util.*;

import static be.twofold.valen.game.idtech.lexer.LexerPunctuation.*;
import static be.twofold.valen.game.idtech.lexer.TokenType.*;
import static org.assertj.core.api.Assertions.*;

class IdLexerPunctuationTest {
    @Test
    void testEveryPunctuationRoundTrips() {
        assertThat(reachable()).allSatisfy(p -> {
            var token = punctuation(p.text());
            assertThat(token.type()).isEqualTo(TT_PUNCTUATION);
            assertThat(token.subtype()).isEqualTo(p.ordinal());
            assertThat(token.value()).isEqualTo(p.text());
        });
    }

    @Test
    void testEveryPunctuationConsumesExactlyItsText() {
        assertThat(reachable()).allSatisfy(p -> {
            var lexer = lexer(p.text() + "1");
            assertThat(lexer.readPunctuation()).isNotNull();
            assertThat(lexer.readNumber().value()).isEqualTo("1");
        });
    }

    @Test
    void testStopsBeforeUnrelatedCharacter() {
        var lexer = lexer("+-");
        assertThat(lexer.readPunctuation().subtype()).isEqualTo(P_ADD.ordinal());
        assertThat(lexer.readPunctuation().subtype()).isEqualTo(P_SUB.ordinal());
    }

    @Test
    void testRejectsUnknownPunctuation() {
        assertThatThrownBy(() -> punctuation("a"))
            .isInstanceOf(LexerException.class)
            .hasMessageContaining("unknown punctuation");
        assertThatThrownBy(() -> punctuation(""))
            .isInstanceOf(LexerException.class)
            .hasMessageContaining("unknown punctuation");
    }

    @Test
    void testReportsPositionOfTokenStart() {
        var lexer = lexer("\n  >>=");
        lexer.readWhiteSpace();
        var token = lexer.readPunctuation();
        assertThat(token.line()).isEqualTo(2);
        assertThat(token.column()).isEqualTo(3);
    }

    private List<LexerPunctuation> reachable() {
        return Arrays.stream(LexerPunctuation.values())
            .filter(p -> p != P_XML_COMMENT)
            .toList();
    }

    private IdLexer lexer(String source, LexerFlags... flags) {
        return new IdLexer(source, flags);
    }

    private IdToken punctuation(String source) {
        return lexer(source).readPunctuation();
    }
}
