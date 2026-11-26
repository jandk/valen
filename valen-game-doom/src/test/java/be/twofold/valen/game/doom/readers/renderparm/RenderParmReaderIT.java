package be.twofold.valen.game.doom.readers.renderparm;

import be.twofold.valen.game.doom.*;
import org.junit.jupiter.api.*;

import java.io.*;

class RenderParmReaderIT {

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new RenderParmReader());
    }

}
