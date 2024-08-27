package org.redeye.valen.game.halflife2;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class HalfLife2GameFactory implements GameFactory<SourceGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("hl2.exe");
    }

    @Override
    public SourceGame load(Path path) throws IOException {
        return new SourceGame(path.getParent(), "hl2");
    }
}
