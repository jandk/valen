package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class StaticModelReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new StaticModelReader(() -> manager, true, true));
    }

}
