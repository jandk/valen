package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

import static org.assertj.core.api.Assertions.*;

class MurmurHash64BTest {
    @ParameterizedTest
    @CsvFileSource(resources = "MurmurHash64B.csv")
    void testMurmurHash64B(int length, String seedString, String expectedString) {
        long seed = Long.parseUnsignedLong(seedString, 16);

        Bytes buffer = XXHashGenerator.generate(length);
        long actual = new MurmurHash64B(seed).hash(buffer).asLong();
        long expected = Long.parseUnsignedLong(expectedString, 16);
        assertThat(actual).isEqualTo(expected);
    }
}
