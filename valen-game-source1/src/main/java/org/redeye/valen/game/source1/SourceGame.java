package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class SourceGame implements Game {
    private final SourceArchive provider;

    public SourceGame(Path path, String mod) throws IOException {
        var resolved = path.resolve(mod).resolve("gameinfo.txt");
        this.provider = new SourceArchive(resolved);
    }

    @Override
    public List<String> archiveNames() {
        return List.of(provider.getName());
    }

    @Override
    public Archive loadArchive(String name) {
        return provider;
    }
}
