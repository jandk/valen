package be.twofold.valen.game;

import be.twofold.valen.core.game.*;
import be.twofold.valen.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final StreamDbCollection streamDbCollection;

    EternalGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.streamDbCollection = StreamDbCollection.load(base, spec);
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps();
    }

    @Override
    public EternalArchive loadArchive(String name) throws IOException {
        var resourcesCollection = ResourcesCollection.load(base, spec, name);

        // TODO: Cast and generic
        return new EternalArchive(streamDbCollection, resourcesCollection);
    }
}
