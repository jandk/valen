package be.twofold.valen.game.doom.readers.material;

import be.twofold.valen.game.doom.*;
import org.junit.jupiter.api.*;

import java.io.*;

class MaterialReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new MaterialReader());
    }

}
