package be.twofold.valen.reader.decl;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class DeclReaderTest {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new DeclReader(() -> manager));
    }
}
