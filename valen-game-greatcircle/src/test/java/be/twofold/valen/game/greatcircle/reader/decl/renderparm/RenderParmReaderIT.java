package be.twofold.valen.game.greatcircle.reader.decl.renderparm;

import be.twofold.valen.game.greatcircle.*;
import org.junit.jupiter.api.*;

import java.io.*;

class RenderParmReaderIT {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new RenderParmReader());
    }
}
