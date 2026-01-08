package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.game.darkages.*;
import org.junit.jupiter.api.*;

import java.io.*;

class ImageReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader((_, store) -> new ImageReader(store, true));
    }

}
