package be.twofold.valen.core.util;

import org.junit.jupiter.api.*;
import wtf.reversed.toolbox.collect.*;

import java.nio.charset.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class Utf8Test {
    @Test
    void byteLengthIsCorrect() {
        for (int cp = 0; cp <= Character.MAX_CODE_POINT; cp++) {
            if (Character.MIN_SURROGATE <= cp && cp <= Character.MAX_SURROGATE) {
                continue;
            }

            var cs = Character.toString(cp);
            assertThat(Utf8.byteLength(cs))
                .withFailMessage(() -> "byteLength is wrong for " + HexFormat.of().withDelimiter(" ").formatHex(cs.getBytes(StandardCharsets.UTF_8)))
                .isEqualTo(cs.getBytes(StandardCharsets.UTF_8).length);
        }
    }

    @Test
    void isValidForAllValidSequencesIsTrue() {
        for (int cp = 0; cp <= Character.MAX_CODE_POINT; cp++) {
            if (Character.MIN_SURROGATE <= cp && cp <= Character.MAX_SURROGATE) {
                continue;
            }

            byte[] bytes = utf8Array(cp);
            var format = HexFormat.of().withDelimiter(" ");
            assertThat(Utf8.isValid(Bytes.wrap(bytes)))
                .withFailMessage(() -> "Invalid but should be valid: " + format.formatHex(bytes))
                .isTrue();
        }
    }

    @Test
    void isValidForAllSurrogatesIsFalse() {
        for (int cp = Character.MIN_SURROGATE; cp <= Character.MAX_SURROGATE; cp++) {
            assertNotValid(utf8Array(cp));
        }
    }

    @Test
    void isValidForAllOverlongIsFalse() {
        for (int cp = 0; cp <= 0x7F; cp++) { // 1-byte long
            assertNotValid(utf8Array(cp, 2));
            assertNotValid(utf8Array(cp, 3));
            assertNotValid(utf8Array(cp, 4));
        }
        for (int cp = 0x80; cp <= 0x7FF; cp++) { // 2-byte long
            assertNotValid(utf8Array(cp, 3));
            assertNotValid(utf8Array(cp, 4));
        }
        for (int cp = 0x800; cp <= 0xFFFF; cp++) { // 2-byte long
            assertNotValid(utf8Array(cp, 4));
        }
    }

    @Test
    void isValidForAllFourByteSequencesOutsideOfUnicodeIsFalse() {
        for (int cp = 0x110000; cp < 0x1FFFFF; cp++) {
            assertNotValid(utf8Array(cp));
        }
    }

    private static byte[] utf8Array(int cp) {
        return utf8Array(cp, utf8Length(cp));
    }

    private static byte[] utf8Array(int cp, int length) {
        if (length < utf8Length(cp)) {
            throw new IllegalArgumentException();
        }
        return switch (length) {
            case 1 -> new byte[]{(byte) cp};
            case 2 -> new byte[]{
                (byte) (0xC0 | (cp & 0x7C0) >> 6),
                (byte) (0x80 | (cp & 0x03F))
            };
            case 3 -> new byte[]{
                (byte) (0xE0 | (cp & 0xF000) >> 12),
                (byte) (0x80 | (cp & 0x0FC0) >> 6),
                (byte) (0x80 | (cp & 0x003F))
            };
            case 4 -> new byte[]{
                (byte) (0xF0 | (cp & 0x1C0000) >> 18),
                (byte) (0x80 | (cp & 0x03F000) >> 12),
                (byte) (0x80 | (cp & 0x000FC0) >> 6),
                (byte) (0x80 | (cp & 0x00003F))
            };
            default -> throw new UnsupportedOperationException();
        };
    }

    private static int utf8Length(int cp) {
        if (cp <= 0x7F) {
            return 1;
        } else if (cp <= 0x7FF) {
            return 2;
        } else if (cp <= 0xFFFF) {
            return 3;
        } else if (cp <= 0x1FFFFF) {
            return 4;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void assertNotValid(byte[] bytes) {
        var format = HexFormat.of().withDelimiter(" ");
        assertThat(Utf8.isValid(Bytes.wrap(bytes)))
            .withFailMessage(() -> "Valid but should be invalid: " + format.formatHex(bytes))
            .isFalse();
    }
}
