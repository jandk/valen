package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class SourceGameFactory implements GameFactory<SourceGame> {
    @Override
    public Set<String> executableNames() {
        return Set.of("hl2.exe", "tf2.exe");
    }

    @Override
    public SourceGame load(Path path) throws IOException {
        return new SourceGame(path.getParent(), findMods(path.getParent()));
    }

    @Override
    public boolean canLoad(Path path) {
        if (!GameFactory.super.canLoad(path)) {
            return false;
        }
        return !findMods(path.getParent()).isEmpty();
    }

    private List<String> findMods(Path basePath) {
        try (var stream = Files.list(basePath)) {
            return stream
                .filter(path -> Files.isRegularFile(path.resolve("gameinfo.txt")))
                .map(path -> path.getFileName().toString())
                .toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
