package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class DeformModelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(archive -> new DeformModelReader(archive, false));
    }

}
