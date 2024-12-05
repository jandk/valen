package be.twofold.valen.core.hashing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class XXHash64Test {
    @ParameterizedTest
    @CsvFileSource(resources = "XXHash64.csv")
    void testXXHash64(int length, String seedString, String expectedString) {
        long seed = Long.parseUnsignedLong(seedString, 16);

        byte[] buffer = XXHashGenerator.generate(length);
        long actual = new XXHash64(seed).hash(buffer, 0, buffer.length).asLong();
        long expected = Long.parseUnsignedLong(expectedString, 16);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testWithOffset() {
        byte[] buffer = "The quick brown fox jumps over the lazy dog".getBytes();
        long actual = new XXHash64(123).hash(buffer, 4, 35).asLong();
        assertThat(actual).isEqualTo(0x4FD1367565445C53L);
    }
}
