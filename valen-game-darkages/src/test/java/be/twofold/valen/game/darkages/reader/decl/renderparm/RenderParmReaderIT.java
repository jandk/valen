package be.twofold.valen.game.darkages.reader.decl.renderparm;

import be.twofold.valen.game.darkages.*;
import org.junit.jupiter.api.*;

import java.io.*;

class RenderParmReaderIT {
    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(_ -> new RenderParmReader());
    }
}
