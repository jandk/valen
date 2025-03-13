package be.twofold.valen.game.eternal;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.packagemapspec.*;
import be.twofold.valen.game.eternal.reader.streamdb.*;
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.eternal.stream.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final Container<Long, StreamDbEntry> streamDbCollection;
    private final Container<EternalAssetID, EternalAsset> commonCollection;

    EternalGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_8_win64.dll"));
        this.streamDbCollection = loadStreams(base, spec, decompressor);
        this.commonCollection = loadResources(base, spec, decompressor, "common", "warehouse");
    }

    static Container<EternalAssetID, EternalAsset> loadResources(Path base, PackageMapSpec spec, Decompressor decompressor, String... names) throws IOException {
        var paths = Arrays.stream(names)
            .flatMap(map -> spec.mapFiles().get(map).stream())
            .filter(file -> file.endsWith(".resources"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<Container<EternalAssetID, EternalAsset>>();
        for (var path : paths) {
            files.add(new ResourcesFile(path, decompressor));
        }
        return Container.compose(files);
    }

    static Container<Long, StreamDbEntry> loadStreams(Path base, PackageMapSpec spec, Decompressor decompressor) throws IOException {
        var paths = spec.files().stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<Container<Long, StreamDbEntry>>();
        for (var path : paths) {
            files.add(new StreamDbFile(path, decompressor));
        }
        return Container.compose(files);
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .filter(map -> spec.mapFiles().get(map).stream()
                .anyMatch(file -> file.endsWith(".resources")))
            .toList();
    }

    @Override
    public EternalArchive loadArchive(String name) throws IOException {
        var resourcesCollection = loadResources(base, spec, decompressor, name);
        return new EternalArchive(streamDbCollection, commonCollection, resourcesCollection, decompressor);
    }
}
