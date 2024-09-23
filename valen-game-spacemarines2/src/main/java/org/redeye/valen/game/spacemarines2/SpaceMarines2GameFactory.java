package org.redeye.valen.game.spacemarines2;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class SpaceMarines2GameFactory implements GameFactory<SpaceMarines2Game> {
    @Override
    public Set<String> executableNames() {
        return Set.of("Warhammer 40000 Space Marine 2.exe");
    }

    @Override
    public SpaceMarines2Game load(Path path) throws IOException {
        return new SpaceMarines2Game(path.getParent());
    }
}
