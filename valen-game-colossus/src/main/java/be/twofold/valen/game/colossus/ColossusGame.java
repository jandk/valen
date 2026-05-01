package be.twofold.valen.game.colossus;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.colossus.reader.image.*;
import be.twofold.valen.game.colossus.reader.packagemapspec.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class ColossusGame implements Game {
    private static final List<AssetReader<?, ColossusAsset>> ASSET_READERS = List.of(
        new ImageReader()
    );

    private final Path base;
    private final PackageMapSpec spec;
    private final ResourcesIndex commonResources;
    private final TexDbIndex commonTexDb;

    ColossusGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpec.read(base.resolve("packagemapspec.json"));
        Decompressors.setOodlePath(path.resolve("oo2core_5_win64.dll"));
        // The engine bootstrap (idResourceManagerLocal::Init1) loads patches
        // newest-first then gameresources last; first match wins, so the
        // newest patch overrides the original.
        this.commonResources = ResourcesIndex.build(commonPaths(".resources"));
        this.commonTexDb = TexDbIndex.build(commonPaths(".texdb"));
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .map(PackageMapSpecMap::name)
            .toList();
    }

    @Override
    public AssetLoader open(String name) throws IOException {
        var map = spec.maps().stream()
            .filter(m -> m.name().equals(name))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("Unknown map: " + name));

        // Base-chunk pseudo-maps ("common", "init") have no per-map files of
        // their own; expose the common pool as the loaded index so archive.all()
        // streams the common assets when browsing them.
        var loadedResources = map.isBaseChunk()
            ? commonResources
            : ResourcesIndex.build(perMapPaths(map, ".resources"));
        var loadedTexDb = map.isBaseChunk()
            ? commonTexDb
            : TexDbIndex.build(perMapPaths(map, ".texdb"));

        var archive = new ColossusArchive(commonResources.index(), loadedResources.index());

        var sources = new HashMap<Path, BinarySource>();
        sources.putAll(commonResources.sources());
        sources.putAll(commonTexDb.sources());
        sources.putAll(loadedResources.sources());
        sources.putAll(loadedTexDb.sources());

        var combinedTexDb = new HashMap<>(loadedTexDb.index());
        commonTexDb.index().forEach(combinedTexDb::putIfAbsent);

        var sharedSources = new HashSet<Path>();
        sharedSources.addAll(commonResources.sources().keySet());
        sharedSources.addAll(commonTexDb.sources().keySet());

        var storageManager = new ColossusStorageManager(sources, sharedSources, combinedTexDb);
        return new AssetLoader(archive, storageManager, List.copyOf(ASSET_READERS));
    }

    // Lists are built lowest-priority first so the index's `put` overwrites
    // give the highest-priority file the final word.
    private List<Path> commonPaths(String suffix) {
        var paths = new ArrayList<Path>();
        addIfExists(paths, "gameresources" + suffix);
        for (int i = 1; i <= spec.patchLevel(); i++) {
            addIfExists(paths, "patch_" + i + suffix);
        }
        return paths;
    }

    private List<Path> perMapPaths(PackageMapSpecMap map, String suffix) {
        var paths = new ArrayList<Path>();
        if (!map.isBaseChunk() && map.chunkId() >= 1) {
            addIfExists(paths, "chunk_" + map.chunkId() + suffix);
        }
        if (map.baseChunkId() > 0) {
            addIfExists(paths, "chunkbase_" + map.baseChunkId() + suffix);
            for (int i = 1; i <= spec.patchLevel(); i++) {
                addIfExists(paths, "patch_" + i + "_chunkbase_" + map.baseChunkId() + suffix);
            }
        }
        return paths;
    }

    private void addIfExists(List<Path> paths, String filename) {
        var path = base.resolve(filename);
        if (Files.exists(path)) {
            paths.add(path);
        }
    }

    @Override
    public void close() {
        Decompressors.resetOodle();
    }
}
