package be.twofold.valen.reader.decl.renderparm;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class RenderParmReaderTest {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new RenderParmReader());
    }
}
