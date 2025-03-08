package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class PortalGameFactory implements GameFactory<SourceGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("hl2.exe");
    }

    @Override
    public SourceGame load(Path path) throws IOException {
        return new SourceGame(path.getParent(), "portal");
    }

    @Override
    public boolean canLoad(Path path) {
        return GameFactory.super.canLoad(path)
            && Files.isDirectory(path.getParent().resolve("portal/bin"));
    }
}
