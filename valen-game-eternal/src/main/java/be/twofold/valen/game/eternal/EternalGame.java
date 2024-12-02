package be.twofold.valen.game.eternal;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.packagemapspec.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final StreamDbCollection streamDbCollection;
    private final ResourcesCollection commonCollection;

    EternalGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_8_win64.dll"));
        this.streamDbCollection = StreamDbCollection.load(base, spec, decompressor);
        this.commonCollection = ResourcesCollection.load(base, spec, decompressor, "common", "warehouse");
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps();
    }

    @Override
    public EternalArchive loadArchive(String name) throws IOException {
        var resourcesCollection = ResourcesCollection.load(base, spec, decompressor, name);
        return new EternalArchive(streamDbCollection, commonCollection, resourcesCollection);
    }
}
