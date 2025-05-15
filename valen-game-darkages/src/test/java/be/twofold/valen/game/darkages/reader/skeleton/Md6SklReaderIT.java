package be.twofold.valen.game.darkages.reader.skeleton;

import be.twofold.valen.game.darkages.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6SklReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(_ -> new Md6SklReader());
    }

}
