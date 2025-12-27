package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.game.Container;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.*;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.File;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.Map;
import be.twofold.valen.game.greatcircle.reader.streamdb.*;
import be.twofold.valen.game.greatcircle.resource.*;
import wtf.reversed.toolbox.compress.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class GreatCircleGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final Container<Long, StreamDbEntry> streamDbCollection;
    private final Container<GreatCircleAssetID, GreatCircleAsset> commonCollection;

    public GreatCircleGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpec.read(base.resolve("packagemapspec_pc.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_9_win64.dll"));
        this.streamDbCollection = loadStreams(base, spec, decompressor);
        this.commonCollection = loadResources(base, spec, decompressor, "common");
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .map(Map::name)
            .toList();
    }

    @Override
    public GreatCircleArchive loadArchive(String name) throws IOException {
        var resourcesCollection = loadResources(base, spec, decompressor, name);
        return new GreatCircleArchive(streamDbCollection, commonCollection, resourcesCollection);
    }

    static Container<GreatCircleAssetID, GreatCircleAsset> loadResources(Path base, PackageMapSpec spec, Decompressor decompressor, String... names) throws IOException {
        var uniqueNames = Set.of(names);
        var fileRefs = spec.maps().stream()
            .filter(m -> uniqueNames.contains(m.name()))
            .flatMap(map -> map.fileRefs().stream())
            .collect(Collectors.toUnmodifiableSet());

        var paths = spec.files().stream()
            .filter(f -> fileRefs.contains(f.id()))
            .map(File::name)
            .filter(s -> s.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<Container<GreatCircleAssetID, GreatCircleAsset>>();
        for (var path : paths) {
            files.add(new ResourcesFile(path, decompressor));
        }
        return Container.compose(files);
    }

    static Container<Long, StreamDbEntry> loadStreams(Path base, PackageMapSpec spec, Decompressor decompressor) throws IOException {
        var paths = spec.files().stream()
            .map(File::name)
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<Container<Long, StreamDbEntry>>();
        for (var path : paths) {
            files.add(new StreamDbFile(path, decompressor));
        }
        return Container.compose(files);
    }
}
