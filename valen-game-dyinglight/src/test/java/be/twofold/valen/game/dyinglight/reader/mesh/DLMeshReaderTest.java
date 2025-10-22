package be.twofold.valen.game.dyinglight.reader.mesh;


import be.twofold.valen.game.dyinglight.*;
import org.junit.jupiter.api.*;

import java.io.*;

class DLMeshReaderTest {
    @Test
    void testCanReadAllModels() throws IOException {
        TestUtils.testReader(archive -> new DLMeshReader());
    }
}
