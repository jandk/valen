package be.twofold.valen.game.greatcircle.reader.md6skl;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6SklReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(_ -> new Md6SklReader());
    }

}
