package be.twofold.valen.game.doom.readers.staticmodel;

import be.twofold.valen.game.doom.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.util.*;

class StaticModelReaderIT {

    // Invalid model, rejected by the game
    private static final Set<String> BROKEN = Set.of(
        "maps/game/classic/e1m2/e1m2_occlusion.bmodel"
    );

    @Test
    void testCanReadAll() throws IOException {
        TestUtils.testReader(new StaticModelReader(false), BROKEN);
    }

}
