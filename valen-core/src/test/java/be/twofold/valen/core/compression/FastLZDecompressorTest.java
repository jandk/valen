package be.twofold.valen.core.compression;

import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

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

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void testFastLZ1(int offset) throws Exception {
        byte[] temp = null;
        try (var input = getClass().getResourceAsStream("ls.fastlz1")) {
            temp = input.readAllBytes();
        }

        var source = new byte[temp.length + 2 * offset];
        System.arraycopy(temp, 0, source, offset, temp.length);
        var target = new byte[LENGTH + 2 * offset];
        decompressor.decompress(
            source, offset, source.length - 2 * offset,
            target, offset, target.length - 2 * offset
        );

        sha256.update(target, offset, LENGTH);
        assertThat(HexFormat.of().formatHex(sha256.digest()))
            .isEqualTo(HASH);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 100})
    void testFastLZ2(int offset) throws Exception {
        byte[] temp = null;
        try (var input = getClass().getResourceAsStream("ls.fastlz2")) {
            temp = input.readAllBytes();
        }

        var source = new byte[temp.length + 2 * offset];
        System.arraycopy(temp, 0, source, offset, temp.length);
        var target = new byte[LENGTH + 2 * offset];
        decompressor.decompress(
            source, offset, source.length - 2 * offset,
            target, offset, target.length - 2 * offset
        );

        sha256.update(target, offset, LENGTH);
        assertThat(HexFormat.of().formatHex(sha256.digest()))
            .isEqualTo(HASH);
    }
}
