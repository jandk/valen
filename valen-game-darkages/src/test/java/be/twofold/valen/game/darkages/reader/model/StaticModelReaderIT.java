package be.twofold.valen.game.darkages.reader.model;

import be.twofold.valen.game.darkages.*;
import org.junit.jupiter.api.*;

import java.io.*;

class StaticModelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new StaticModelReader(false));
    }

}
