package be.twofold.valen.core.util;

/**
 * The narrow {@code <ctype.h>} classifications, over ASCII only.
 */
public final class ASCII {
    private ASCII() {
    }

    /**
     * Whether the character is a letter or a digit.
     */
    public static boolean isAlNum(char c) {
        return isAlpha(c) || isDigit(c);
    }

    /**
     * Whether the character is a letter, {@code A-Z} or {@code a-z}.
     */
    public static boolean isAlpha(char c) {
        return isLower(c) || isUpper(c);
    }

    /**
     * Whether the character is in the 7 bit ASCII range.
     */
    public static boolean isAscii(char c) {
        return c <= 0x7F;
    }

    /**
     * Whether the character separates words within a line, space or tab.
     */
    public static boolean isBlank(char c) {
        return c == ' ' || c == '\t';
    }

    /**
     * Whether the character is a control character, {@code 00-1F} or {@code 7F}.
     */
    public static boolean isCntrl(char c) {
        return isAscii(c) && !isPrint(c);
    }

    /**
     * Whether the character is a decimal digit, {@code 0-9}.
     */
    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * Whether the character has a visible glyph, so printable but not space.
     */
    public static boolean isGraph(char c) {
        return c >= '!' && c <= '~';
    }

    /**
     * Whether the character is a lowercase letter, {@code a-z}.
     */
    public static boolean isLower(char c) {
        return c >= 'a' && c <= 'z';
    }

    /**
     * Whether the character is printable, including space.
     */
    public static boolean isPrint(char c) {
        return isGraph(c) || c == ' ';
    }

    /**
     * Whether the character is punctuation, so visible but not alphanumeric.
     */
    public static boolean isPunct(char c) {
        return isGraph(c) && !isAlNum(c);
    }

    /**
     * Whether the character is whitespace, space or {@code 09-0D}.
     */
    public static boolean isSpace(char c) {
        return isBlank(c) || (c >= '\n' && c <= '\r');
    }

    /**
     * Whether the character is an uppercase letter, {@code A-Z}.
     */
    public static boolean isUpper(char c) {
        return c >= 'A' && c <= 'Z';
    }

    /**
     * Whether the character is a hexadecimal digit, {@code 0-9}, {@code A-F} or {@code a-f}.
     */
    public static boolean isXDigit(char c) {
        return isDigit(c) || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
    }

    /**
     * The lowercase form of a letter, or the character unchanged.
     */
    public static char toLower(char c) {
        return isUpper(c) ? (char) (c + ('a' - 'A')) : c;
    }

    /**
     * The uppercase form of a letter, or the character unchanged.
     */
    public static char toUpper(char c) {
        return isLower(c) ? (char) (c - ('a' - 'A')) : c;
    }
}
