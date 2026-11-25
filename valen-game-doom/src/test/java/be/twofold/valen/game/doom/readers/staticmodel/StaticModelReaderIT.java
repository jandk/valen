package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.game.doom.*;
import org.junit.jupiter.api.*;

import java.io.*;

class StaticModelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new StaticModelReader());
    }

}
