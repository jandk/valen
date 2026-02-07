package be.twofold.valen.game.greatcircle;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.game.io.*;
import be.twofold.valen.game.greatcircle.reader.decl.*;
import be.twofold.valen.game.greatcircle.reader.decl.material2.*;
import be.twofold.valen.game.greatcircle.reader.decl.renderparm.*;
import be.twofold.valen.game.greatcircle.reader.deformmodel.*;
import be.twofold.valen.game.greatcircle.reader.hair.*;
import be.twofold.valen.game.greatcircle.reader.image.*;
import be.twofold.valen.game.greatcircle.reader.md6mesh.*;
import be.twofold.valen.game.greatcircle.reader.md6skl.*;
import be.twofold.valen.game.greatcircle.reader.packagemapspec.*;
import be.twofold.valen.game.greatcircle.reader.staticmodel.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class GreatCircleGame implements Game {
    private static final DeclReader DECL_READER = new DeclReader();
    private static final List<AssetReader<?, GreatCircleAsset>> ASSET_READERS = List.of(
        new DeformModelReader(true),
        new HairReader(),
        new ImageReader(true),
        new MaterialReader(DECL_READER),
        new Md6MeshReader(true),
        new Md6SklReader(),
        new RenderParmReader(),
        new StaticModelReader(true)
    );

    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final StreamDbIndex streamDbIndex;
    private final ResourcesIndex commonResources;

    public GreatCircleGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpec.read(base.resolve("packagemapspec_pc.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_9_win64.dll"));
        this.streamDbIndex = loadStreamDbIndex(base, spec);
        this.commonResources = ResourcesIndex.build(base, filterResources(spec, "common"));
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .map(SpecMap::name)
            .toList();
    }

    @Override
    public AssetLoader open(String name) throws IOException {
        var loadedResources = ResourcesIndex.build(base, filterResources(spec, name));

        var archive = new GreatCircleArchive(
            Map.copyOf(commonResources.index()),
            Map.copyOf(loadedResources.index())
        );

        var sources = new HashMap<FileId, BinarySource>();
        sources.putAll(streamDbIndex.sources());
        sources.putAll(commonResources.sources());
        sources.putAll(loadedResources.sources());
        var storageManager = new GreatCircleStorageManager(
            sources,
            streamDbIndex.sources().keySet(),
            streamDbIndex.index()
        );

        return new AssetLoader(archive, storageManager, List.copyOf(ASSET_READERS));
    }

    private StreamDbIndex loadStreamDbIndex(Path base, PackageMapSpec spec) throws IOException {
        var paths = spec.files().stream()
            .map(SpecFile::name)
            .filter(s -> s.endsWith(".streamdb"))
            .toList();

        return StreamDbIndex.build(base, paths);
    }

    private List<String> filterResources(PackageMapSpec spec, String... names) {
        var uniqueNames = Set.of(names);
        var fileRefs = spec.maps().stream()
            .filter(map -> uniqueNames.contains(map.name()))
            .flatMap(map -> map.fileRefs().stream())
            .collect(Collectors.toUnmodifiableSet());

        return spec.files().stream()
            .filter(f -> fileRefs.contains(f.id()))
            .map(SpecFile::name)
            .filter(s -> s.endsWith(".resources"))
            .toList();
    }


    @Override
    public void close() {
        // Unload for the next game
        Decompressors.resetOodle();
        DECL_READER.clearCache();
    }
}
