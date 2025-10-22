package be.twofold.valen.game.dyinglight;

import be.twofold.valen.core.game.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public class DyingLightGame implements Game {
    private final Path game;
    private final List<PackInfo> packs;

    public DyingLightGame(Path game) throws IOException {
        this.game = game;
        this.packs = collectResourcePacks(game);
    }

    @Override
    public List<String> archiveNames() {
        return packs.stream()
            .map(PackInfo::name)
            .toList();
    }

    @Override
    public DyingLightArchive loadArchive(String name) throws IOException {
        PackInfo pack = packs.stream()
            .filter(p -> p.name().equals(name))
            .findFirst().orElseThrow();
        return new DyingLightArchive(pack.path());
    }

    private static List<PackInfo> collectResourcePacks(Path game) throws IOException {
        Path path = game.resolve("work/data_platform/pc/assets");
        try (Stream<Path> stream = Files.walk(path)) {
            return stream
                .filter(p -> p.getFileName().toString().endsWith(".rpack"))
                .map(p -> new PackInfo(path.relativize(p).toString(), p))
                .toList();
        }
    }

    private record PackInfo(String name, Path path) {
    }
}
