package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6AnimReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new Md6AnimReader());
    }

}
