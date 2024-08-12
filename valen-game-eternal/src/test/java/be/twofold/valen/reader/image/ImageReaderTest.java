package be.twofold.valen.reader.image;

import be.twofold.valen.*;
import be.twofold.valen.game.eternal.reader.image.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new ImageReader(archive, false));
    }

}
