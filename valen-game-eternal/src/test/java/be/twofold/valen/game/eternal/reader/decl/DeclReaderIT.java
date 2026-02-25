package be.twofold.valen.game.eternal.reader.decl;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class DeclReaderIT {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new DeclReader());
    }
}
