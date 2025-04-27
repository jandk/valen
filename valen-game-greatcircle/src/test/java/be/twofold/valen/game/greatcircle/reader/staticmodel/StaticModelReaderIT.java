package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class StaticModelReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(manager -> new StaticModelReader(manager, false));
    }

}
