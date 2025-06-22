package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.game.darkages.*;
import org.junit.jupiter.api.*;

import java.io.*;

class VegetationReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new VegetationReader(archive, false));
    }

}
