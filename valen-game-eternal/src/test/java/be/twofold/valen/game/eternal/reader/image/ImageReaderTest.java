package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.game.eternal.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new ImageReader(archive, false));
    }

}
