package be.twofold.valen.game.eternal;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.eternal.reader.binaryfile.*;
import be.twofold.valen.game.eternal.reader.decl.*;
import be.twofold.valen.game.eternal.reader.decl.material2.*;
import be.twofold.valen.game.eternal.reader.decl.renderparm.*;
import be.twofold.valen.game.eternal.reader.file.FileReader;
import be.twofold.valen.game.eternal.reader.filecompressed.*;
import be.twofold.valen.game.eternal.reader.image.*;
import be.twofold.valen.game.eternal.reader.json.*;
import be.twofold.valen.game.eternal.reader.mapfilestaticinstances.*;
import be.twofold.valen.game.eternal.reader.md6anim.*;
import be.twofold.valen.game.eternal.reader.md6model.*;
import be.twofold.valen.game.eternal.reader.md6skel.*;
import be.twofold.valen.game.eternal.reader.packagemapspec.*;
import be.twofold.valen.game.eternal.reader.staticmodel.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalGame implements Game {
    private static final AssetReaders ASSET_READERS;

    static {
        var declReader = new DeclReader();
        ASSET_READERS = new AssetReaders(List.of(
            declReader,
            // Binary converters
            new BinaryFileReader(),
            new FileCompressedReader(),
            new FileReader(),
            new JsonReader(),

            // Actual readers
            new ImageReader(true),
            new MapFileStaticInstancesReader(),
            new MaterialReader(declReader),
            new Md6AnimReader(),
            new Md6ModelReader(true),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(true)
        ));
    }

    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final StreamDbIndex streamDbIndex;
    private final ResourcesIndex commonResources;

    EternalGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_8_win64.dll"));
        this.streamDbIndex = loadStreamDbIndex(base, spec);
        this.commonResources = ResourcesIndex.build(base, filterResources(spec, "common", "warehouse"));
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .filter(map -> spec.mapFiles().get(map).stream()
                .anyMatch(file -> file.endsWith(".resources")))
            .toList();
    }

    public AssetLoader open(String name) throws IOException {
        var loadedResources = ResourcesIndex.build(base, filterResources(spec, name));

        var archive = new EternalArchive(
            Map.copyOf(commonResources.index()),
            Map.copyOf(loadedResources.index())
        );

        var sources = new HashMap<FileId, BinarySource>();
        sources.putAll(streamDbIndex.sources());
        sources.putAll(commonResources.sources());
        sources.putAll(loadedResources.sources());
        var storageManager = new EternalStorageManager(
            sources,
            streamDbIndex.sources().keySet(),
            decompressor,
            streamDbIndex.index()
        );

        return new AssetLoader(archive, storageManager, ASSET_READERS);
    }

    private StreamDbIndex loadStreamDbIndex(Path base, PackageMapSpec spec) throws IOException {
        var paths = spec.files().stream()
            .filter(s -> s.endsWith(".streamdb"))
            .toList();

        return StreamDbIndex.build(base, paths);
    }

    private List<String> filterResources(PackageMapSpec spec, String... names) {
        return Arrays.stream(names)
            .flatMap(map -> spec.mapFiles().get(map).stream())
            .filter(file -> file.endsWith(".resources"))
            .toList();
    }
}
