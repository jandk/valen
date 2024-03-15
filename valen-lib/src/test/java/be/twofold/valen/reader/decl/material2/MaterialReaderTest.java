package be.twofold.valen.reader.decl.material2;

import be.twofold.valen.*;
import be.twofold.valen.reader.decl.*;
import org.junit.jupiter.api.*;

import java.io.*;

class MaterialReaderTest {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> {
            var declReader = new DeclReader(manager.resourceManager);
            return new MaterialReader(manager.resourceManager, declReader);
        });
    }
}
