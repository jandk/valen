package be.twofold.valen.core.util;

import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

class ASCIITest {
    private static final String DIGIT = "0123456789";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String PUNCT = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    private static final String ALPHA = UPPER + LOWER;
    private static final String ALNUM = ALPHA + DIGIT;
    private static final String GRAPH = ALNUM + PUNCT;
    private static final String PRINT = GRAPH + " ";
    private static final String XDIGIT = DIGIT + "ABCDEF" + "abcdef";
    private static final String BLANK = " " + range(0x09, 0x09);

    private static final String SPACE = " " + range(0x09, 0x0D);
    private static final String ASCII_RANGE = range(0x00, 0x7F);
    private static final String CNTRL = range(0x00, 0x1F) + range(0x7F, 0x7F);

    @Test
    void isAlNumAcceptsLettersAndDigits() {
        assertAccepts("isAlNum", ASCII::isAlNum, ALNUM);
    }

    @Test
    void isAlphaAcceptsLetters() {
        assertAccepts("isAlpha", ASCII::isAlpha, ALPHA);
    }

    @Test
    void isAsciiAcceptsTheLowest128() {
        assertAccepts("isAscii", ASCII::isAscii, ASCII_RANGE);
    }

    @Test
    void isBlankAcceptsSpaceAndTab() {
        assertAccepts("isBlank", ASCII::isBlank, BLANK);
    }

    @Test
    void isCntrlAcceptsControlCharacters() {
        assertAccepts("isCntrl", ASCII::isCntrl, CNTRL);
    }

    @Test
    void isDigitAcceptsDecimalDigits() {
        assertAccepts("isDigit", ASCII::isDigit, DIGIT);
    }

    @Test
    void isGraphAcceptsVisibleCharacters() {
        assertAccepts("isGraph", ASCII::isGraph, GRAPH);
    }

    @Test
    void isLowerAcceptsLowercaseLetters() {
        assertAccepts("isLower", ASCII::isLower, LOWER);
    }

    @Test
    void isPrintAcceptsVisibleCharactersAndSpace() {
        assertAccepts("isPrint", ASCII::isPrint, PRINT);
    }

    @Test
    void isPunctAcceptsPunctuation() {
        assertAccepts("isPunct", ASCII::isPunct, PUNCT);
    }

    @Test
    void isSpaceAcceptsWhitespace() {
        assertAccepts("isSpace", ASCII::isSpace, SPACE);
    }

    @Test
    void isUpperAcceptsUppercaseLetters() {
        assertAccepts("isUpper", ASCII::isUpper, UPPER);
    }

    @Test
    void isXDigitAcceptsHexadecimalDigits() {
        assertAccepts("isXDigit", ASCII::isXDigit, XDIGIT);
    }

    @Test
    void toLowerChangesUppercaseLettersOnly() {
        assertMaps("toLower", ASCII::toLower, c -> {
            int index = UPPER.indexOf(c);
            return index < 0 ? c : LOWER.charAt(index);
        });
    }

    @Test
    void toUpperChangesLowercaseLettersOnly() {
        assertMaps("toUpper", ASCII::toUpper, c -> {
            int index = LOWER.indexOf(c);
            return index < 0 ? c : UPPER.charAt(index);
        });
    }

    private static void assertAccepts(String name, CharPredicate actual, String members) {
        for (int i = 0; i <= Character.MAX_VALUE; i++) {
            char c = (char) i;
            assertThat(actual.test(c))
                .withFailMessage(() -> String.format("%s is wrong for U+%04X", name, (int) c))
                .isEqualTo(members.indexOf(c) >= 0);
        }
    }

    private static void assertMaps(String name, CharMapper actual, CharMapper expected) {
        for (int i = 0; i <= Character.MAX_VALUE; i++) {
            char c = (char) i;
            assertThat(actual.map(c))
                .withFailMessage(() -> String.format("%s is wrong for U+%04X", name, (int) c))
                .isEqualTo(expected.map(c));
        }
    }

    private static String range(int from, int to) {
        var builder = new StringBuilder();
        for (int i = from; i <= to; i++) {
            builder.append((char) i);
        }
        return builder.toString();
    }

    @FunctionalInterface
    private interface CharPredicate {
        boolean test(char c);
    }

    @FunctionalInterface
    private interface CharMapper {
        char map(char c);
    }
}
