package be.twofold.valen.core.util.hash;

import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class XXHash64Test {

    @ParameterizedTest
    @CsvFileSource(resources = "XXHash64.csv")
    void testXXHash64(int length, String seedString, String expectedString) {
        long seed = Long.parseUnsignedLong(seedString, 16);

        byte[] buffer = XXHashGenerator.generate(length);
        long actual = XXHash64.hash(buffer, 0, buffer.length, seed);
        long expected = Long.parseUnsignedLong(expectedString, 16);
        assertThat(actual).isEqualTo(expected);
    }

}
