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
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalGame implements Game {
    private static final DeclReader DECL_READER = new DeclReader();
    private static final List<AssetReader<?, EternalAsset>> ASSET_READERS = List.of(
        DECL_READER,
        // Binary converters
        new BinaryFileReader(),
        new FileCompressedReader(),
        new FileReader(),
        new JsonReader(),

        // Actual readers
        new ImageReader(true),
        new MapFileStaticInstancesReader(),
        new MaterialReader(DECL_READER),
        new Md6AnimReader(),
        new Md6ModelReader(true),
        new Md6SkelReader(),
        new RenderParmReader(),
        new StaticModelReader(true)
    );

    private final Path base;
    private final PackageMapSpec spec;
    private final StreamDbIndex streamDbIndex;
    private final ResourcesIndex commonResources;
    private final Decompressors decompressors;

    EternalGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.streamDbIndex = loadStreamDbIndex(base, spec);
        this.commonResources = ResourcesIndex.build(filterResources(spec, "common", "warehouse"));
        this.decompressors = new Decompressors(path.resolve("oo2core_8_win64.dll"));
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .filter(map -> spec.mapFiles().get(map).stream()
                .anyMatch(file -> file.endsWith(".resources")))
            .toList();
    }

    public AssetLoader open(String name) throws IOException {
        var loadedResources = ResourcesIndex.build(filterResources(spec, name));

        var archive = Archive.layered(
            Archive.of(loadedResources.assets()),
            Archive.of(commonResources.assets())
        );

        var sources = new HashMap<Path, BinarySource>();
        sources.putAll(streamDbIndex.sources());
        sources.putAll(commonResources.sources());
        sources.putAll(loadedResources.sources());
        var storageManager = new EternalStorageManager(
            sources,
            streamDbIndex.sources().keySet(),
            streamDbIndex.index(),
            decompressors
        );

        return new AssetLoader(archive, storageManager, List.copyOf(ASSET_READERS));
    }

    private StreamDbIndex loadStreamDbIndex(Path base, PackageMapSpec spec) throws IOException {
        var paths = spec.files().stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        return StreamDbIndex.build(paths);
    }

    private List<Path> filterResources(PackageMapSpec spec, String... names) {
        return Arrays.stream(names)
            .flatMap(map -> spec.mapFiles().get(map).stream())
            .filter(file -> file.endsWith(".resources"))
            .map(base::resolve)
            .toList();
    }

    @Override
    public void close() {
        // Unload for the next game
        DECL_READER.clearCache();
    }
}
