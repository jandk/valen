package be.twofold.valen.game.idtech.lexer;

import org.junit.jupiter.api.*;

import static be.twofold.valen.game.idtech.lexer.LexerFlags.*;
import static be.twofold.valen.game.idtech.lexer.NumberType.*;
import static be.twofold.valen.game.idtech.lexer.TokenType.*;
import static org.assertj.core.api.Assertions.*;

class IdLexerNumberTest {
    private static IdLexer lexer(String source, LexerFlags... flags) {
        return new IdLexer(source, flags);
    }

    private static IdToken number(String source, LexerFlags... flags) {
        return lexer(source, flags).readNumber();
    }

    @Test
    void testReadsDecimalInteger() {
        var token = number("123");
        assertThat(token.type()).isEqualTo(TT_NUMBER);
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_INTEGER);
        assertThat(token.value()).isEqualTo("123");
    }

    @Test
    void testReadsHex() {
        var token = number("0x1F");
        assertThat(token.subtype()).isEqualTo(TT_HEX | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0x1F");
    }

    @Test
    void testReadsHexWithUppercasePrefix() {
        var token = number("0XdeadBEEF");
        assertThat(token.subtype()).isEqualTo(TT_HEX | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0XdeadBEEF");
    }

    @Test
    void testHexStopsAtNonHexDigit() {
        var token = number("0x1Fg");
        assertThat(token.subtype()).isEqualTo(TT_HEX | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0x1F");
    }

    @Test
    void testHexStopsWithNoDigitsAtAll() {
        var token = number("0xG");
        assertThat(token.subtype()).isEqualTo(TT_HEX | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0x");
    }

    @Test
    void testReadsBinary() {
        var token = number("0b1011");
        assertThat(token.subtype()).isEqualTo(TT_BINARY | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0b1011");
    }

    @Test
    void testBinaryStopsAtTwo() {
        var token = number("0b1012");
        assertThat(token.subtype()).isEqualTo(TT_BINARY | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0b101");
    }

    @Test
    void testBinaryStopsWithNoDigitsAtAll() {
        var token = number("0b2");
        assertThat(token.subtype()).isEqualTo(TT_BINARY | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0b");
    }

    @Test
    void testReadsOctal() {
        var token = number("0755");
        assertThat(token.subtype()).isEqualTo(TT_OCTAL | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0755");
    }

    @Test
    void testOctalStopsAtEight() {
        var token = number("0778");
        assertThat(token.subtype()).isEqualTo(TT_OCTAL | TT_INTEGER);
        assertThat(token.value()).isEqualTo("077");
    }

    @Test
    void testBareZeroIsOctal() {
        var token = number("0");
        assertThat(token.subtype()).isEqualTo(TT_OCTAL | TT_INTEGER);
        assertThat(token.value()).isEqualTo("0");
    }

    @Test
    void testZeroFollowedByDotIsDecimal() {
        var token = number("0.5");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("0.5");
    }

    @Test
    void testReadsFloat() {
        var token = number("1.5");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("1.5");
    }

    @Test
    void testReadsLeadingDotFloat() {
        var token = number(".5");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo(".5");
    }

    @Test
    void testReadsTrailingDotFloat() {
        var token = number("5.");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("5.");
    }

    @Test
    void testSinglePrecisionSuffix() {
        var token = number("1.5f");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_SINGLE_PRECISION);
        assertThat(token.value()).isEqualTo("1.5");
    }

    @Test
    void testExtendedPrecisionSuffix() {
        var token = number("1.5L");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_EXTENDED_PRECISION);
        assertThat(token.value()).isEqualTo("1.5");
    }

    @Test
    void testHalfPrecisionSuffix() {
        var token = number("1.5h");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_HALF_PRECISION);
        assertThat(token.value()).isEqualTo("1.5");
    }

    @Test
    void testReadsExponent() {
        var token = number("1.5e10");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("1.5e10");
    }

    @Test
    void testReadsNegativeExponent() {
        var token = number("1.5e-3");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("1.5e-3");
    }

    @Test
    void testReadsPositiveExponent() {
        var token = number("1.5e+3");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("1.5e+3");
    }

    @Test
    void testExponentWithoutDotIsFloat() {
        var token = number("1e5");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("1e5");
    }

    @Test
    void testExponentAfterSeveralDotsIsFloat() {
        var token = number("1.2.3e");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION);
        assertThat(token.value()).isEqualTo("1.2.3e");
    }

    @Test
    void testUnsignedSuffix() {
        var token = number("5u");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_INTEGER | TT_UNSIGNED);
        assertThat(token.value()).isEqualTo("5");
    }

    @Test
    void testLongSuffix() {
        var token = number("5L");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_INTEGER | TT_LONG);
        assertThat(token.value()).isEqualTo("5");
    }

    @Test
    void testUnsignedLongLongSuffix() {
        var token = number("5ull");
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_INTEGER | TT_UNSIGNED | TT_LONG);
        assertThat(token.value()).isEqualTo("5");
    }

    @Test
    void testIntegerSuffixStopsAfterThree() {
        var lexer = lexer("5ulluXu");
        assertThat(lexer.readNumber().subtype()).isEqualTo(TT_DECIMAL | TT_INTEGER | TT_UNSIGNED | TT_LONG);
        assertThat(lexer.readString('u').value()).isEqualTo("X");
    }

    @Test
    void testReadsIpAddress() {
        var token = number("127.0.0.1", LEXFL_ALLOWIPADDRESSES);
        assertThat(token.subtype()).isEqualTo(TT_IPADDRESS);
        assertThat(token.value()).isEqualTo("127.0.0.1");
    }

    @Test
    void testReadsIpAddressWithPort() {
        var token = number("127.0.0.1:8080", LEXFL_ALLOWIPADDRESSES);
        assertThat(token.subtype()).isEqualTo(TT_IPADDRESS | TT_IPPORT);
        assertThat(token.value()).isEqualTo("127.0.0.1:8080");
    }

    @Test
    void testRejectsTwoDotsWithoutIpAddresses() {
        assertThat(number("1.2.3")).isNull();
    }

    @Test
    void testRejectsIpAddressWithoutThreeDots() {
        assertThat(number("1.2.3.4.5", LEXFL_ALLOWIPADDRESSES)).isNull();
    }

    @Test
    void testReadsInfinite() {
        var token = number("1.#INF", LEXFL_ALLOWFLOATEXCEPTIONS);
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION | TT_INFINITE);
        assertThat(token.value()).isEqualTo("1.#INF");
    }

    @Test
    void testReadsIndefinite() {
        var token = number("1.#IND", LEXFL_ALLOWFLOATEXCEPTIONS);
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION | TT_INDEFINITE);
        assertThat(token.value()).isEqualTo("1.#IND");
    }

    @Test
    void testReadsNan() {
        var token = number("1.#NAN", LEXFL_ALLOWFLOATEXCEPTIONS);
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION | TT_NAN);
        assertThat(token.value()).isEqualTo("1.#NAN");
    }

    @Test
    void testReadsQuietNan() {
        var token = number("1.#QNAN", LEXFL_ALLOWFLOATEXCEPTIONS);
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION | TT_NAN);
        assertThat(token.value()).isEqualTo("1.#QNAN");
    }

    @Test
    void testReadsSignallingNan() {
        var token = number("1.#SNAN", LEXFL_ALLOWFLOATEXCEPTIONS);
        assertThat(token.subtype()).isEqualTo(TT_DECIMAL | TT_FLOAT | TT_DOUBLE_PRECISION | TT_NAN);
        assertThat(token.value()).isEqualTo("1.#SNAN");
    }

    @Test
    void testReportsPositionOfTokenStart() {
        var lexer = lexer("\n  1.5f");
        lexer.readWhiteSpace();
        var token = lexer.readNumber();
        assertThat(token.line()).isEqualTo(2);
        assertThat(token.column()).isEqualTo(3);
    }

    @Test
    void testLeavesTerminatorUnconsumed() {
        var lexer = lexer("123\"x\"");
        assertThat(lexer.readNumber().value()).isEqualTo("123");
        assertThat(lexer.readString('"').value()).isEqualTo("x");
    }

    @Test
    void testLeavesTerminatorUnconsumedAfterFloatSuffix() {
        var lexer = lexer("1.5f\"x\"");
        assertThat(lexer.readNumber().value()).isEqualTo("1.5");
        assertThat(lexer.readString('"').value()).isEqualTo("x");
    }

    @Test
    void testLeavesTerminatorUnconsumedAfterIntegerSuffix() {
        var lexer = lexer("5u\"x\"");
        assertThat(lexer.readNumber().value()).isEqualTo("5");
        assertThat(lexer.readString('"').value()).isEqualTo("x");
    }
}
