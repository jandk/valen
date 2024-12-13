package org.redeye.valen.game.halflife;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class HalfLifeGameFactory implements GameFactory<HalfLifeMod> {

    @Override
    public Set<String> executableNames() {
        return Set.of("hl.exe");
    }

    @Override
    public boolean canLoad(Path path) {
        return path.getFileName().toString().startsWith("client");
    }

    @Override
    public HalfLifeMod load(Path path) throws IOException {
        return new HalfLifeMod(path.getParent().getParent());
    }
}
