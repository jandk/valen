package be.twofold.valen.core.hashing;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class XXHash32Test {

    @ParameterizedTest
    @CsvFileSource(resources = "XXHash32.csv")
    void testXXHash32(int length, String seedString, String expectedString) {
        int seed = Integer.parseUnsignedInt(seedString, 16);

        byte[] buffer = XXHashGenerator.generate(length);
        int actual = new XXHash32(seed).hash(buffer, 0, buffer.length).asInt();
        int expected = Integer.parseUnsignedInt(expectedString, 16);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void testWithOffset() {
        byte[] buffer = "The quick brown fox jumps over the lazy dog".getBytes();
        int actual = new XXHash32(123).hash(buffer, 4, 35).asInt();
        assertThat(actual).isEqualTo(0xE6C0EA2E);
    }

}
