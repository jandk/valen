package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class StaticModelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new StaticModelReader(false));
    }

}
