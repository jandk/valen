package be.twofold.valen.game.doom.readers.md6model;

import be.twofold.valen.game.doom.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6ModelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new Md6ModelReader());
    }

}
