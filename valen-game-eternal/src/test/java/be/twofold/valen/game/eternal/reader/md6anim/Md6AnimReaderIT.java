package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6AnimReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(_ -> new Md6AnimReader());
    }

}
