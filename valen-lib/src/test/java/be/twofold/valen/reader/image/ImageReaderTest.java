package be.twofold.valen.reader.image;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new ImageReader(null));
    }

}
