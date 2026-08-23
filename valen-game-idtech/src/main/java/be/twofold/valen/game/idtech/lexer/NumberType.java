package be.twofold.valen.game.idtech.lexer;

final class NumberType {
    static final int TT_INTEGER = 1 << 0;
    static final int TT_DECIMAL = 1 << 1;
    static final int TT_HEX = 1 << 2;
    static final int TT_OCTAL = 1 << 3;
    static final int TT_BINARY = 1 << 4;
    static final int TT_LONG = 1 << 5;
    static final int TT_UNSIGNED = 1 << 6;
    static final int TT_FLOAT = 1 << 7;
    static final int TT_SINGLE_PRECISION = 1 << 8;
    static final int TT_DOUBLE_PRECISION = 1 << 9;
    static final int TT_EXTENDED_PRECISION = 1 << 10;
    static final int TT_INFINITE = 1 << 11;
    static final int TT_INDEFINITE = 1 << 12;
    static final int TT_NAN = 1 << 13;
    static final int TT_IPADDRESS = 1 << 14;
    static final int TT_IPPORT = 1 << 15;
    static final int TT_VALUESVALID = 1 << 16;
    static final int TT_HALF_PRECISION = 1 << 17;

    private NumberType() {
    }
}
