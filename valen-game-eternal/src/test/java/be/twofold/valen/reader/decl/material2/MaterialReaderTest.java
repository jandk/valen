package be.twofold.valen.reader.decl.material2;

import be.twofold.valen.*;
import be.twofold.valen.reader.decl.*;
import org.junit.jupiter.api.*;

import java.io.*;

class MaterialReaderTest {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> {
            var declReader = new DeclReader(archive);
            return new MaterialReader(archive, declReader);
        });
    }
}
