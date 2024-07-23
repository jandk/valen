package be.twofold.valen.reader.md6anim;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6AnimReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new Md6AnimReader());
    }

}
