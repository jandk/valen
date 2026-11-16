package be.twofold.valen.game.eternal.reader.md6skel;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6SkelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new Md6SkelReader());
    }

}
