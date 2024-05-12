package be.twofold.valen.reader.md6model;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6ModelReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new Md6ModelReader(manager.streamManager));
    }

}
