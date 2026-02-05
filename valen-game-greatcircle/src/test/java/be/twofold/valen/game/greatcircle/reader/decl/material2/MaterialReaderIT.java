package be.twofold.valen.game.greatcircle.reader.decl.material2;

import be.twofold.valen.game.greatcircle.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import org.junit.jupiter.api.*;

import java.io.*;

class MaterialReaderIT {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(_ -> {
            var declReader = new DeclReader();
            return new MaterialReader(declReader);
        });
    }
}
