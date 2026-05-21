package be.twofold.valen.game.greatcircle.reader.md6mesh;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6MeshReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new Md6MeshReader(false));
    }

}
