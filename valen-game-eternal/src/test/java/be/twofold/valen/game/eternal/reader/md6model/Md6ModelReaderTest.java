package be.twofold.valen.game.eternal.reader.md6model;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6ModelReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(Md6ModelReader::new);
    }

}
