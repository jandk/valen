package be.twofold.valen.core.util.hash;

import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class XXHash64Test {

    private static final long PRIME32 = 0x000000009E3779B1L;
    private static final long PRIME64 = 0x9E3779B185EBCA8DL;

    @ParameterizedTest
    @CsvFileSource(resources = "XXHash64.csv")
    void testXXHash64(int length, String seedString, String expectedString) {
        long seed = Long.parseUnsignedLong(seedString, 16);
        long expected = Long.parseUnsignedLong(expectedString, 16);

        byte[] buffer = generateBuffer(length);
        long actual = XXHash64.hash(buffer, 0, buffer.length, seed);
        assertThat(actual)
            .isEqualTo(expected);
    }

    private byte[] generateBuffer(int length) {
        byte[] buffer = new byte[length];
        long byteGen = PRIME32;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (byteGen >> 56);
            byteGen *= PRIME64;
        }
        return buffer;
    }

}
