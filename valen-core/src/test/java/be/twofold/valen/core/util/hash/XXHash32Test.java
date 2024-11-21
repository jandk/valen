package be.twofold.valen.core.util.hash;

import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class XXHash32Test {

    @ParameterizedTest
    @CsvFileSource(resources = "XXHash32.csv")
    void testXXHash32(int length, String seedString, String expectedString) {
        int seed = Integer.parseUnsignedInt(seedString, 16);

        byte[] buffer = XXHashGenerator.generate(length);
        int actual = XXHash32.hash(buffer, 0, buffer.length, seed);
        int expected = Integer.parseUnsignedInt(expectedString, 16);
        assertThat(actual).isEqualTo(expected);
    }

}
