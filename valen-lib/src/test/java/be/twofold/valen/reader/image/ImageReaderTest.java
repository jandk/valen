package be.twofold.valen.reader.image;

import be.twofold.valen.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderTest {

    @Test
    void testCanReadAll() throws IOException {
        System.out.println(System.getProperty("user.dir"));
        TestUtils.testReader(manager -> new ImageReader(null));
    }

}
