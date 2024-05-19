package be.twofold.valen.reader.decl.entitydef;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class EntityDefReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new EntityDefReader());
    }

}
