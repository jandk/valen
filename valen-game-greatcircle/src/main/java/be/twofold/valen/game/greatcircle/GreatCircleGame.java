package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.File;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.Map;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GreatCircleGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;

    public GreatCircleGame(Path path) {
        this.base = path.resolve("base");
        this.spec = PackageMapSpec.read(base.resolve("packagemapspec_pc.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_9_win64.dll"));
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .map(Map::name)
            .toList();
    }

    @Override
    public Archive loadArchive(String name) throws IOException {
        var map = spec.maps().stream()
            .filter(m -> m.name().equals(name))
            .findFirst().orElseThrow();

        var files = spec.files().stream()
            .filter(f -> map.fileRefs().contains(f.id()))
            .map(File::name)
            .toList();

        var streamDbs = files.stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();
        var streamDbCollection = StreamDbCollection.load(streamDbs, decompressor);

        var resources = files.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();
        var resourcesCollection = ResourcesCollection.load(resources, decompressor);

        return new GreatCircleArchive(streamDbCollection, resourcesCollection);
    }
}
