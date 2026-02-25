package be.twofold.valen.game.darkages.reader.strandshair;

import be.twofold.valen.game.darkages.*;
import org.junit.jupiter.api.*;

import java.io.*;

class StrandsHairReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new StrandsHairReader());
    }

}
