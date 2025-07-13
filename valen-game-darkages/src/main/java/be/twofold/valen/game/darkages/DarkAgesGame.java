package be.twofold.valen.game.darkages;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.packagemapspec.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.darkages.reader.streamdb.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DarkAgesGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final Container<Long, StreamDbEntry> streamDbCollection;
    private final Container<DarkAgesAssetID, DarkAgesAsset> commonCollection;

    DarkAgesGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle();
        this.streamDbCollection = loadStreams(base, spec, decompressor);
        this.commonCollection = loadResources(base, spec, decompressor, "common", "warehouse", "init");
    }

    static Container<DarkAgesAssetID, DarkAgesAsset> loadResources(Path base, PackageMapSpec spec, Decompressor decompressor, String... names) throws IOException {
        var paths = Arrays.stream(names)
                .flatMap(map -> spec.mapFiles().get(map).stream())
                .filter(file -> file.endsWith(".resources"))
                .map(base::resolve)
                .toList();

        var files = new ArrayList<Container<DarkAgesAssetID, DarkAgesAsset>>();
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
                .map(s -> s.equals("common") ? "gameresources" : s)
                .toList();
    }

    @Override
    public DarkAgesArchive loadArchive(String name) throws IOException {
        if (name.equals("gameresources")) {
            name = "common";
        }
        var resourcesCollection = loadResources(base, spec, decompressor, name);
        return new DarkAgesArchive(streamDbCollection, commonCollection, resourcesCollection, decompressor);
    }
}
