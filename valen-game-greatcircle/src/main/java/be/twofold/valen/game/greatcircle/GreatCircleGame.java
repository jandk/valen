package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.game.Container;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.*;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.File;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.Map;
import be.twofold.valen.game.greatcircle.reader.streamdb.*;
import be.twofold.valen.game.greatcircle.resource.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class GreatCircleGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;

    public GreatCircleGame(Path path) throws IOException {
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
    public GreatCircleArchive loadArchive(String name) throws IOException {
        var common = spec.maps().stream()
            .filter(m -> m.name().equals("common"))
            .findFirst().orElseThrow();

        var map = spec.maps().stream()
            .filter(m -> m.name().equals(name))
            .findFirst().orElseThrow();

        var files = spec.files().stream()
            .filter(f -> map.fileRefs().contains(f.id()) || common.fileRefs().contains(f.id()))
            .map(File::name)
            .toList();

        var streamDbCollection = loadStreamDBs(files);
        var resourcesCollection = loadResources(files);
        return new GreatCircleArchive(streamDbCollection, resourcesCollection);
    }

    private Container<Long, StreamDbEntry> loadStreamDBs(List<String> filenames) throws IOException {
        var paths = filenames.stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var containers = new ArrayList<Container<Long, StreamDbEntry>>();
        for (Path path : paths) {
            containers.add(new StreamDbFile(path, decompressor));
        }
        return Container.compose(containers);
    }

    private Container<GreatCircleAssetID, GreatCircleAsset> loadResources(List<String> filenames) throws IOException {
        var paths = filenames.stream()
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var containers = new ArrayList<Container<GreatCircleAssetID, GreatCircleAsset>>();
        for (Path path : paths) {
            containers.add(new ResourcesFile(path, decompressor));
        }
        return Container.compose(containers);
    }
}
