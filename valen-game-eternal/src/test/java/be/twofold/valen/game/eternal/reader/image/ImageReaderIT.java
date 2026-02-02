package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new ImageReader(true));
    }

}
