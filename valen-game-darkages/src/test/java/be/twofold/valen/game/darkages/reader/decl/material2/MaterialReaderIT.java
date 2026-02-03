package be.twofold.valen.game.darkages.reader.decl.material2;

import be.twofold.valen.game.darkages.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import org.junit.jupiter.api.*;

import java.io.*;

class MaterialReaderIT {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> {
            var declReader = new DeclReader();
            return new MaterialReader(declReader);
        });
    }
}
