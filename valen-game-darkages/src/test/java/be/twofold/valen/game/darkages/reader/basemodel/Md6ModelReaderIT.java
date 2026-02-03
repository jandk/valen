package be.twofold.valen.game.darkages.reader.basemodel;

import be.twofold.valen.game.darkages.*;
import org.junit.jupiter.api.*;

import java.io.*;

class Md6ModelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new Md6ModelReader(false));
    }

}
