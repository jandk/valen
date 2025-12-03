package be.twofold.valen.core.compression;

import be.twofold.valen.core.util.collect.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import java.io.*;
import java.security.*;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

class FastLZDecompressorTest {
    private static final String HASH = "12a6d908a68ccf6f9f3d799705577c28763f5deef6eddcff7643d6d8a6de543d";
    private static final int LENGTH = 138216;

    private final MessageDigest sha256 = MessageDigest.getInstance("SHA256");
    private final FastLZDecompressor decompressor = new FastLZDecompressor();

    FastLZDecompressorTest() throws NoSuchAlgorithmException {
    }

    @Test
    void testLiteral() throws IOException {
        var source = new byte[]{0x02, 0x41, 0x42, 0x43};
        var expected = new byte[]{0x41, 0x42, 0x43};
        var target = new byte[expected.length];

        decompressor.decompress(Bytes.wrap(source), MutableBytes.wrap(target));

        assertThat(target).isEqualTo(expected);
    }

    @Test
    void testShortMatch1() throws IOException {
        var source = new byte[]{0x03, 0x41, 0x42, 0x43, 0x44, 0x20, 0x02};
        var expected = new byte[]{0x41, 0x42, 0x43, 0x44, 0x42, 0x43, 0x44};
        var target = new byte[expected.length];

        decompressor.decompress(Bytes.wrap(source), MutableBytes.wrap(target));

        assertThat(target).isEqualTo(expected);
    }

    @Test
    void testShortMatch2() throws IOException {
        var source = new byte[]{0x00, 0x61, 0x40, 0x00};
        var expected = new byte[]{0x61, 0x61, 0x61, 0x61, 0x61};
        var target = new byte[expected.length];

        decompressor.decompress(Bytes.wrap(source), MutableBytes.wrap(target));

        assertThat(target).isEqualTo(expected);
    }

    @Test
    void testLongMatch() throws IOException {
        var source = new byte[]{0x01, 0x44, 0x45, (byte) 0xE0, 0x01, 0x01};
        var expected = new byte[]{0x44, 0x45, 0x44, 0x45, 0x44, 0x45, 0x44, 0x45, 0x44, 0x45, 0x44, 0x45};
        var target = new byte[expected.length];

        decompressor.decompress(Bytes.wrap(source), MutableBytes.wrap(target));

        assertThat(target).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void testFastLZ1(int offset) throws Exception {
        byte[] temp;
        try (var input = getClass().getResourceAsStream("ls.fastlz1")) {
            temp = input.readAllBytes();
        }

        var source = new byte[temp.length + 2 * offset];
        System.arraycopy(temp, 0, source, offset, temp.length);
        var target = new byte[LENGTH + 2 * offset];
        var src = Bytes.wrap(source, offset, source.length - 2 * offset);
        var dst = MutableBytes.wrap(target, offset, target.length - 2 * offset);
        decompressor.decompress(src, dst);

        sha256.update(target, offset, LENGTH);
        assertThat(HexFormat.of().formatHex(sha256.digest()))
            .isEqualTo(HASH);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void testFastLZ2(int offset) throws Exception {
        byte[] temp;
        try (var input = getClass().getResourceAsStream("ls.fastlz2")) {
            temp = input.readAllBytes();
        }

        var source = new byte[temp.length + 2 * offset];
        System.arraycopy(temp, 0, source, offset, temp.length);
        var target = new byte[LENGTH + 2 * offset];
        var src = Bytes.wrap(source, offset, source.length - 2 * offset);
        var dst = MutableBytes.wrap(target, offset, target.length - 2 * offset);
        decompressor.decompress(src, dst);

        sha256.update(target, offset, LENGTH);
        assertThat(HexFormat.of().formatHex(sha256.digest()))
            .isEqualTo(HASH);
    }
}
