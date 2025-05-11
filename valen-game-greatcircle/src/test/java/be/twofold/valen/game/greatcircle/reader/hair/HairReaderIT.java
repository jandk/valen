package be.twofold.valen.game.greatcircle.reader.hair;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class HairReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(HairReader::new);
    }

}
