package org.redeye.valen.game.source1;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class SourceGameFactory implements GameFactory<SourceGame> {
    private static final Map<String, List<String>> MODS = Map.of(
        "hl2.exe", List.of("hl2", "portal"),
        "tf2.exe", List.of("tf2")
    );

    @Override
    public Set<String> executableNames() {
        return MODS.keySet();
    }

    @Override
    public SourceGame load(Path path) throws IOException {
        return new SourceGame(path.getParent(), findMod(path).orElseThrow());
    }

    @Override
    public boolean canLoad(Path path) {
        if (!GameFactory.super.canLoad(path)) {
            return false;
        }
        return findMod(path).isPresent();
    }

    private Optional<String> findMod(Path path) {
        return MODS.get(path.getFileName().toString()).stream()
            .filter(mod -> Files.isDirectory(path.getParent().resolve(mod).resolve("bin")))
            .findFirst();
    }
}
