package be.twofold.valen.game.doom.readers.image;

import be.twofold.valen.game.doom.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new ImageReader());
    }

}
