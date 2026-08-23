package be.twofold.valen.game.idtech.lexer;

import wtf.reversed.toolbox.util.*;

import static be.twofold.valen.game.idtech.lexer.NumberType.*;
import static be.twofold.valen.game.idtech.lexer.TokenType.*;

public record IdToken(
    TokenType type,
    int subtype,
    String value,
    int line,
    int column
) {
    public int getIntValue() {
        return Math.toIntExact(getLongValue());
    }

    public long getLongValue() {
        Check.state(type == TT_NUMBER, "type != TT_NUMBER");

        if ((subtype & TT_FLOAT) != 0) {
            float f = getFloatValue();
            return Float.isFinite(f) ? (long) f : Long.MIN_VALUE;
        }
        if ((subtype & TT_DECIMAL) != 0) {
            return Long.parseUnsignedLong(value);
        }
        if ((subtype & TT_IPADDRESS) != 0) {
            return parseIpAddress();
        }

        int begin;
        int radix;
        if ((subtype & TT_HEX) != 0) {
            begin = 2;
            radix = 16;
        } else if ((subtype & TT_OCTAL) != 0) {
            begin = 1;
            radix = 8;
        } else if ((subtype & TT_BINARY) != 0) {
            begin = 2;
            radix = 2;
        } else {
            throw new UnsupportedOperationException();
        }

        return begin >= value.length()
            ? 0
            : Long.parseUnsignedLong(value, begin, value.length(), radix);
    }

    public float getFloatValue() {
        Check.state(type == TT_NUMBER, "type != TT_NUMBER");

        if ((subtype & TT_FLOAT) != 0) {
            if ((subtype & TT_INFINITE) != 0) {
                return Float.POSITIVE_INFINITY;
            }
            if ((subtype & TT_INDEFINITE) != 0) {
                // negative NaN, cool
                return Float.intBitsToFloat(0xFFC0_0000);
            }
            if ((subtype & TT_NAN) != 0) {
                return Float.NaN;
            }

            try {
                return Float.parseFloat(value);
            } catch (NumberFormatException e) {
                return 0.0f;
            }
        } else if ((subtype & TT_DECIMAL) != 0) {
            return Float.parseFloat(value);
        } else {
            long l = getLongValue();
            return l >= 0 ? l : (l >>> 1 | l & 1) * 2.0f;
        }
    }

    private long parseIpAddress() {
        int p = 0;
        int c = 0;
        long result = 0;
        while (at(p) != '\0' && at(p) != ':') {
            if (at(p) == '.') {
                while (c != 3) {
                    result *= 10;
                    c++;
                }
                c = 0;
            } else {
                result = result * 10 + (at(p) - '0');
                c++;
            }
            p++;
        }
        while (c != 3) {
            result *= 10;
            c++;
        }
        return result;
    }

    private char at(int i) {
        return i < value.length() ? value.charAt(i) : '\0';
    }
}
