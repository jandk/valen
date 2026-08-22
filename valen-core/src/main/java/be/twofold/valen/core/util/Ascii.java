package be.twofold.valen.core.util;

/**
 * Utility class for handling and determining properties of ASCII characters.
 * <p>
 * Basically what ctype.h supports, but with a few extra methods.
 */
public final class Ascii {
    private static final int CNTRL = 0x0100;
    private static final int SPACE = 0x0200;
    private static final int BLANK = 0x0400;
    private static final int PUNCT = 0x0800;
    private static final int ALPHA = 0x1000;
    private static final int UNDER = 0x2000;
    private static final int XDIGIT = 0x4000;

    /**
     * Optimized table, with the minimum set of flags.
     */
    private static final short[] TABLE = {
        CNTRL,                 // NULL
        CNTRL,                 // START OF HEADING
        CNTRL,                 // START OF TEXT
        CNTRL,                 // END OF TEXT
        CNTRL,                 // END OF TRANSMISSION
        CNTRL,                 // ENQUIRY
        CNTRL,                 // ACKNOWLEDGE
        CNTRL,                 // BEL
        CNTRL,                 // BACKSPACE
        CNTRL + SPACE + BLANK, // CHARACTER TABULATION
        CNTRL + SPACE,         // LINE FEED (LF)
        CNTRL + SPACE,         // LINE TABULATION
        CNTRL + SPACE,         // FORM FEED (FF)
        CNTRL + SPACE,         // CARRIAGE RETURN (CR)
        CNTRL,                 // SHIFT OUT
        CNTRL,                 // SHIFT IN
        CNTRL,                 // DATA LINK ESCAPE
        CNTRL,                 // DEVICE CONTROL ONE
        CNTRL,                 // DEVICE CONTROL TWO
        CNTRL,                 // DEVICE CONTROL THREE
        CNTRL,                 // DEVICE CONTROL FOUR
        CNTRL,                 // NEGATIVE ACKNOWLEDGE
        CNTRL,                 // SYNCHRONOUS IDLE
        CNTRL,                 // END OF TRANSMISSION BLOCK
        CNTRL,                 // CANCEL
        CNTRL,                 // END OF MEDIUM
        CNTRL,                 // SUBSTITUTE
        CNTRL,                 // ESCAPE
        CNTRL,                 // INFORMATION SEPARATOR FOUR
        CNTRL,                 // INFORMATION SEPARATOR THREE
        CNTRL,                 // INFORMATION SEPARATOR TWO
        CNTRL,                 // INFORMATION SEPARATOR ONE
        BLANK,                 // SPACE
        PUNCT,                 // EXCLAMATION MARK
        PUNCT,                 // QUOTATION MARK
        PUNCT,                 // NUMBER SIGN
        PUNCT,                 // DOLLAR SIGN
        PUNCT,                 // PERCENT SIGN
        PUNCT,                 // AMPERSAND
        PUNCT,                 // APOSTROPHE
        PUNCT,                 // LEFT PARENTHESIS
        PUNCT,                 // RIGHT PARENTHESIS
        PUNCT,                 // ASTERISK
        PUNCT,                 // PLUS SIGN
        PUNCT,                 // COMMA
        PUNCT,                 // HYPHEN-MINUS
        PUNCT,                 // FULL STOP
        PUNCT,                 // SOLIDUS
        XDIGIT,                // DIGIT ZERO
        XDIGIT + 1,            // DIGIT ONE
        XDIGIT + 2,            // DIGIT TWO
        XDIGIT + 3,            // DIGIT THREE
        XDIGIT + 4,            // DIGIT FOUR
        XDIGIT + 5,            // DIGIT FIVE
        XDIGIT + 6,            // DIGIT SIX
        XDIGIT + 7,            // DIGIT SEVEN
        XDIGIT + 8,            // DIGIT EIGHT
        XDIGIT + 9,            // DIGIT NINE
        PUNCT,                 // COLON
        PUNCT,                 // SEMICOLON
        PUNCT,                 // LESS-THAN SIGN
        PUNCT,                 // EQUALS SIGN
        PUNCT,                 // GREATER-THAN SIGN
        PUNCT,                 // QUESTION MARK
        PUNCT,                 // COMMERCIAL AT
        ALPHA + XDIGIT + 10,   // LATIN CAPITAL LETTER A
        ALPHA + XDIGIT + 11,   // LATIN CAPITAL LETTER B
        ALPHA + XDIGIT + 12,   // LATIN CAPITAL LETTER C
        ALPHA + XDIGIT + 13,   // LATIN CAPITAL LETTER D
        ALPHA + XDIGIT + 14,   // LATIN CAPITAL LETTER E
        ALPHA + XDIGIT + 15,   // LATIN CAPITAL LETTER F
        ALPHA + 16,            // LATIN CAPITAL LETTER G
        ALPHA + 17,            // LATIN CAPITAL LETTER H
        ALPHA + 18,            // LATIN CAPITAL LETTER I
        ALPHA + 19,            // LATIN CAPITAL LETTER J
        ALPHA + 20,            // LATIN CAPITAL LETTER K
        ALPHA + 21,            // LATIN CAPITAL LETTER L
        ALPHA + 22,            // LATIN CAPITAL LETTER M
        ALPHA + 23,            // LATIN CAPITAL LETTER N
        ALPHA + 24,            // LATIN CAPITAL LETTER O
        ALPHA + 25,            // LATIN CAPITAL LETTER P
        ALPHA + 26,            // LATIN CAPITAL LETTER Q
        ALPHA + 27,            // LATIN CAPITAL LETTER R
        ALPHA + 28,            // LATIN CAPITAL LETTER S
        ALPHA + 29,            // LATIN CAPITAL LETTER T
        ALPHA + 30,            // LATIN CAPITAL LETTER U
        ALPHA + 31,            // LATIN CAPITAL LETTER V
        ALPHA + 32,            // LATIN CAPITAL LETTER W
        ALPHA + 33,            // LATIN CAPITAL LETTER X
        ALPHA + 34,            // LATIN CAPITAL LETTER Y
        ALPHA + 35,            // LATIN CAPITAL LETTER Z
        PUNCT,                 // LEFT SQUARE BRACKET
        PUNCT,                 // REVERSE SOLIDUS
        PUNCT,                 // RIGHT SQUARE BRACKET
        PUNCT,                 // CIRCUMFLEX ACCENT
        PUNCT + UNDER,         // LOW LINE
        PUNCT,                 // GRAVE ACCENT
        ALPHA + XDIGIT + 10,   // LATIN SMALL LETTER A
        ALPHA + XDIGIT + 11,   // LATIN SMALL LETTER B
        ALPHA + XDIGIT + 12,   // LATIN SMALL LETTER C
        ALPHA + XDIGIT + 13,   // LATIN SMALL LETTER D
        ALPHA + XDIGIT + 14,   // LATIN SMALL LETTER E
        ALPHA + XDIGIT + 15,   // LATIN SMALL LETTER F
        ALPHA + 16,            // LATIN SMALL LETTER G
        ALPHA + 17,            // LATIN SMALL LETTER H
        ALPHA + 18,            // LATIN SMALL LETTER I
        ALPHA + 19,            // LATIN SMALL LETTER J
        ALPHA + 20,            // LATIN SMALL LETTER K
        ALPHA + 21,            // LATIN SMALL LETTER L
        ALPHA + 22,            // LATIN SMALL LETTER M
        ALPHA + 23,            // LATIN SMALL LETTER N
        ALPHA + 24,            // LATIN SMALL LETTER O
        ALPHA + 25,            // LATIN SMALL LETTER P
        ALPHA + 26,            // LATIN SMALL LETTER Q
        ALPHA + 27,            // LATIN SMALL LETTER R
        ALPHA + 28,            // LATIN SMALL LETTER S
        ALPHA + 29,            // LATIN SMALL LETTER T
        ALPHA + 30,            // LATIN SMALL LETTER U
        ALPHA + 31,            // LATIN SMALL LETTER V
        ALPHA + 32,            // LATIN SMALL LETTER W
        ALPHA + 33,            // LATIN SMALL LETTER X
        ALPHA + 34,            // LATIN SMALL LETTER Y
        ALPHA + 35,            // LATIN SMALL LETTER Z
        PUNCT,                 // LEFT CURLY BRACKET
        PUNCT,                 // VERTICAL LINE
        PUNCT,                 // RIGHT CURLY BRACKET
        PUNCT,                 // TILDE
        CNTRL,                 // DELETE
    };

    private Ascii() {
    }

    /**
     * Whether the character is a letter or a digit.
     */
    public static boolean isAlNum(int cp) {
        return is(cp, ALPHA | XDIGIT);
    }

    /**
     * Whether the character is a letter, {@code A-Z} or {@code a-z}.
     */
    public static boolean isAlpha(int cp) {
        return is(cp, ALPHA);
    }

    /**
     * Whether the character is in the 7 bit ASCII range.
     */
    public static boolean isAscii(int cp) {
        return (cp & ~0x7F) == 0;
    }

    /**
     * Whether the character separates words within a line, space or tab.
     */
    public static boolean isBlank(int cp) {
        return is(cp, BLANK);
    }

    /**
     * Whether the character is a control character, {@code 00-1F} or {@code 7F}.
     */
    public static boolean isCntrl(int cp) {
        return is(cp, CNTRL);
    }

    /**
     * Whether the character is a decimal digit, {@code 0-9}.
     */
    public static boolean isDigit(int cp) {
        return isRange(cp, '0', '9');
    }

    /**
     * Whether the character has a visible glyph, so printable but not space.
     */
    public static boolean isGraph(int cp) {
        return isRange(cp, '!', '~');
    }

    /**
     * Whether the character is a lowercase letter, {@code a-z}.
     */
    public static boolean isLower(int cp) {
        return isRange(cp, 'a', 'z');
    }

    /**
     * Whether the character is printable, including space.
     */
    public static boolean isPrint(int cp) {
        return isRange(cp, ' ', '~');
    }

    /**
     * Whether the character is punctuation, so visible but not alphanumeric.
     */
    public static boolean isPunct(int cp) {
        return is(cp, PUNCT);
    }

    /**
     * Whether the character is whitespace, space or {@code 09-0D}.
     */
    public static boolean isSpace(int cp) {
        return is(cp, SPACE | BLANK);
    }

    /**
     * Whether the character is an uppercase letter, {@code A-Z}.
     */
    public static boolean isUpper(int cp) {
        return isRange(cp, 'A', 'Z');
    }

    /**
     * Whether the character is a hexadecimal digit, {@code 0-9}, {@code A-F} or {@code a-f}.
     */
    public static boolean isHexDigit(int cp) {
        return is(cp, XDIGIT);
    }

    /**
     * The lowercase form of a letter, or the character unchanged.
     */
    public static int toLower(int cp) {
        return isUpper(cp) ? cp + 0x20 : cp;
    }

    /**
     * The uppercase form of a letter, or the character unchanged.
     */
    public static int toUpper(int cp) {
        return isLower(cp) ? cp - 0x20 : cp;
    }

    // Non ctype.h functions

    /**
     * Whether the character is an octal digit, {@code 0-7}.
     */
    public static boolean isOctDigit(int cp) {
        return isRange(cp, '0', '7');
    }

    /**
     * Whether the character can appear in an identifier, so a letter, a digit or {@code _}.
     */
    public static boolean isWord(int cp) {
        return is(cp, ALPHA | UNDER | XDIGIT);
    }

    /**
     * The base 36 value of a letter or a digit, so {@code 0-9} for {@code 0-9} and
     * {@code 10-35} for {@code A-Z} or {@code a-z}, or {@code -1} for anything else.
     */
    public static int toDigit(int cp) {
        return isAlNum(cp) ? TABLE[cp] & 0xFF : -1;
    }

    // Helper methods

    private static boolean is(int cp, int mask) {
        return isAscii(cp) && (TABLE[cp] & mask) != 0;
    }

    /**
     * Branchless {@code lo <= cp && cp <= hi}.
     */
    private static boolean isRange(int cp, int lo, int hi) {
        return ((cp - lo) | (hi - cp)) >= 0;
    }
}
