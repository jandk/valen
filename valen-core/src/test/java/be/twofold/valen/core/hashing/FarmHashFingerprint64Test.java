package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;
import org.junit.jupiter.api.*;

import java.nio.charset.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Modified from <a href="https://github.com/google/guava/blob/master/guava-tests/test/com/google/common/hash/FarmHashFingerprint64Test.java">Guava</a>
 */
class FarmHashFingerprint64Test {
    private static final HashFunction HASH_FUNCTION = HashFunction.farmHashFingerprint64();

    @Test
    void testSimple() {
        assertThat(fingerprint("test".getBytes(StandardCharsets.UTF_8))).isEqualTo(0x7717383DAA85B5B2L);
        assertThat(fingerprint("test".repeat(8).getBytes(StandardCharsets.UTF_8))).isEqualTo(0xC5C3F54CD962178AL);
        assertThat(fingerprint("test".repeat(64).getBytes(StandardCharsets.UTF_8))).isEqualTo(0x30944D44BD63FCE7L);
    }

    @Test
    void testMultipleLengths() {
        int iterations = 800;
        byte[] buf = new byte[iterations * 4];
        int bufLen = 0;
        long h = 0;
        for (int i = 0; i < iterations; ++i) {
            h ^= fingerprint(buf, i);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            h ^= fingerprint(buf, i * i % bufLen);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            h ^= fingerprint(buf, i * i * i % bufLen);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            h ^= fingerprint(buf, bufLen);
            h = remix(h);
            buf[bufLen++] = getChar(h);

            int x0 = Byte.toUnsignedInt(buf[bufLen - 1]);
            int x1 = Byte.toUnsignedInt(buf[bufLen - 2]);
            int x2 = Byte.toUnsignedInt(buf[bufLen - 3]);
            int x3 = Byte.toUnsignedInt(buf[bufLen / 2]);
            buf[((x0 << 16) + (x1 << 8) + x2) % bufLen] ^= (byte) x3;
            buf[((x1 << 16) + (x2 << 8) + x3) % bufLen] ^= (byte) (i % 256);
        }
        assertThat(h).isEqualTo(0x7a1d67c50ec7e167L);
    }

    private static long fingerprint(byte[] bytes) {
        return fingerprint(bytes, bytes.length);
    }

    private static long fingerprint(byte[] bytes, int length) {
        return HASH_FUNCTION.hash(Bytes.wrap(bytes, 0, length)).asLong();
    }

    private static long remix(long h) {
        h ^= h >>> 41;
        h *= 0x389EA8BB;
        return h;
    }

    private static byte getChar(long h) {
        return (byte) ('a' + ((h & 0xfffff) % 26));
    }
}
