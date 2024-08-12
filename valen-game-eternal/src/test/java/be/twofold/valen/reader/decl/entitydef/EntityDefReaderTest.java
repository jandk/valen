package be.twofold.valen.reader.decl.entitydef;

import be.twofold.valen.*;
import be.twofold.valen.game.eternal.reader.decl.entitydef.*;
import org.junit.jupiter.api.*;

import java.io.*;

class EntityDefReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new EntityDefReader());
    }

}
