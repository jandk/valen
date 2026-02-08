package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new ImageReader(true));
    }

}
