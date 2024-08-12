package be.twofold.valen.game.eternal.reader.decl.renderparm;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class RenderParmReaderTest {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new RenderParmReader());
    }
}
