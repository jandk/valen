package be.twofold.valen.game.greatcircle.reader.decl;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class DeclReaderIT {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new DeclReader());
    }
}
