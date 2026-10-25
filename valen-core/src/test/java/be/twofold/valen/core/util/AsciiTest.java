package be.twofold.valen.core.util;

import org.junit.jupiter.api.*;

import java.util.function.*;

import static org.assertj.core.api.Assertions.*;

class AsciiTest {
    private static final String DIGIT = "0123456789";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String PUNCT = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private static final String ALPHA = UPPER + LOWER;
    private static final String ALNUM = ALPHA + DIGIT;
    private static final String GRAPH = ALNUM + PUNCT;
    private static final String PRINT = GRAPH + " ";
    private static final String XDIGIT = DIGIT + "ABCDEF" + "abcdef";
    private static final String OCTDIGIT = "01234567";
    private static final String WORD = ALNUM + "_";
    private static final String BLANK = " " + range(0x09, 0x09);

    private static final String SPACE = " " + range(0x09, 0x0D);
    private static final String ASCII_RANGE = range(0x00, 0x7F);
    private static final String CNTRL = range(0x00, 0x1F) + range(0x7F, 0x7F);

    private static final int[] OUTSIDE = {
        Integer.MIN_VALUE, Integer.MIN_VALUE + 1, -0x110000, -0x80, -1,
        0x10000, 0x1F600, 0x10FFFF, Integer.MAX_VALUE - 1, Integer.MAX_VALUE,
    };

    @Test
    void isAlNumAcceptsLettersAndDigits() {
        assertAccepts("isAlNum", Ascii::isAlNum, ALNUM);
    }

    @Test
    void isAlphaAcceptsLetters() {
        assertAccepts("isAlpha", Ascii::isAlpha, ALPHA);
    }

    @Test
    void isAsciiAcceptsTheLowest128() {
        assertAccepts("isAscii", Ascii::isAscii, ASCII_RANGE);
    }

    @Test
    void isBlankAcceptsSpaceAndTab() {
        assertAccepts("isBlank", Ascii::isBlank, BLANK);
    }

    @Test
    void isCntrlAcceptsControlCharacters() {
        assertAccepts("isCntrl", Ascii::isCntrl, CNTRL);
    }

    @Test
    void isDigitAcceptsDecimalDigits() {
        assertAccepts("isDigit", Ascii::isDigit, DIGIT);
    }

    @Test
    void isGraphAcceptsVisibleCharacters() {
        assertAccepts("isGraph", Ascii::isGraph, GRAPH);
    }

    @Test
    void isLowerAcceptsLowercaseLetters() {
        assertAccepts("isLower", Ascii::isLower, LOWER);
    }

    @Test
    void isPrintAcceptsVisibleCharactersAndSpace() {
        assertAccepts("isPrint", Ascii::isPrint, PRINT);
    }

    @Test
    void isPunctAcceptsPunctuation() {
        assertAccepts("isPunct", Ascii::isPunct, PUNCT);
    }

    @Test
    void isSpaceAcceptsWhitespace() {
        assertAccepts("isSpace", Ascii::isSpace, SPACE);
    }

    @Test
    void isUpperAcceptsUppercaseLetters() {
        assertAccepts("isUpper", Ascii::isUpper, UPPER);
    }

    @Test
    void isHexDigitAcceptsHexadecimalDigits() {
        assertAccepts("isHexDigit", Ascii::isHexDigit, XDIGIT);
    }

    @Test
    void isOctDigitAcceptsOctalDigits() {
        assertAccepts("isOctDigit", Ascii::isOctDigit, OCTDIGIT);
    }

    @Test
    void isWordAcceptsLettersDigitsAndUnderscore() {
        assertAccepts("isWord", Ascii::isWord, WORD);
    }

    @Test
    void toLowerChangesUppercaseLettersOnly() {
        assertMaps("toLower", Ascii::toLower, c -> {
            int index = UPPER.indexOf(c);
            return index < 0 ? c : LOWER.charAt(index);
        });
    }

    @Test
    void toUpperChangesLowercaseLettersOnly() {
        assertMaps("toUpper", Ascii::toUpper, c -> {
            int index = LOWER.indexOf(c);
            return index < 0 ? c : UPPER.charAt(index);
        });
    }

    @Test
    void toDigitMapsLettersAndDigitsToTheirBase36Value() {
        assertMaps("toDigit", Ascii::toDigit, c -> {
            int digit = DIGIT.indexOf(c);
            if (digit >= 0) {
                return digit;
            }
            int lower = LOWER.indexOf(c);
            if (lower >= 0) {
                return lower + 10;
            }
            int upper = UPPER.indexOf(c);
            return upper < 0 ? -1 : upper + 10;
        });
    }

    @Test
    void everyClassRejectsCodePointsOutsideTheCharRange() {
        for (int cp : OUTSIDE) {
            assertRejects("isAlNum", Ascii::isAlNum, cp);
            assertRejects("isAlpha", Ascii::isAlpha, cp);
            assertRejects("isAscii", Ascii::isAscii, cp);
            assertRejects("isBlank", Ascii::isBlank, cp);
            assertRejects("isCntrl", Ascii::isCntrl, cp);
            assertRejects("isDigit", Ascii::isDigit, cp);
            assertRejects("isGraph", Ascii::isGraph, cp);
            assertRejects("isHexDigit", Ascii::isHexDigit, cp);
            assertRejects("isLower", Ascii::isLower, cp);
            assertRejects("isOctDigit", Ascii::isOctDigit, cp);
            assertRejects("isPrint", Ascii::isPrint, cp);
            assertRejects("isPunct", Ascii::isPunct, cp);
            assertRejects("isSpace", Ascii::isSpace, cp);
            assertRejects("isUpper", Ascii::isUpper, cp);
            assertRejects("isWord", Ascii::isWord, cp);
        }
    }

    @Test
    void theConversionsPassThroughCodePointsOutsideTheCharRange() {
        for (int cp : OUTSIDE) {
            assertThat(Ascii.toLower(cp))
                .withFailMessage(() -> String.format("toLower is wrong for %d", cp))
                .isEqualTo(cp);
            assertThat(Ascii.toUpper(cp))
                .withFailMessage(() -> String.format("toUpper is wrong for %d", cp))
                .isEqualTo(cp);
            assertThat(Ascii.toDigit(cp))
                .withFailMessage(() -> String.format("toDigit is wrong for %d", cp))
                .isEqualTo(-1);
        }
    }

    private static void assertAccepts(String name, IntPredicate actual, String members) {
        for (int i = 0; i <= Character.MAX_VALUE; i++) {
            char c = (char) i;
            assertThat(actual.test(c))
                .withFailMessage(() -> String.format("%s is wrong for U+%04X", name, (int) c))
                .isEqualTo(members.indexOf(c) >= 0);
        }
    }

    private static void assertMaps(String name, IntUnaryOperator actual, IntUnaryOperator expected) {
        for (int i = 0; i <= Character.MAX_VALUE; i++) {
            char c = (char) i;
            assertThat(actual.applyAsInt(c))
                .withFailMessage(() -> String.format("%s is wrong for U+%04X", name, (int) c))
                .isEqualTo(expected.applyAsInt(c));
        }
    }

    private static void assertRejects(String name, IntPredicate actual, int cp) {
        assertThat(actual.test(cp))
            .withFailMessage(() -> String.format("%s is wrong for %d", name, cp))
            .isFalse();
    }

    private static String range(int from, int to) {
        var builder = new StringBuilder();
        for (int i = from; i <= to; i++) {
            builder.append((char) i);
        }
        return builder.toString();
    }
}
