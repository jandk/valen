package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.game.darkages.reader.anim.*;
import be.twofold.valen.game.darkages.reader.basemodel.*;
import be.twofold.valen.game.darkages.reader.binaryfile.*;
import be.twofold.valen.game.darkages.reader.bink.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import be.twofold.valen.game.darkages.reader.decl.material2.*;
import be.twofold.valen.game.darkages.reader.decl.renderparm.*;
import be.twofold.valen.game.darkages.reader.image.*;
import be.twofold.valen.game.darkages.reader.mask.*;
import be.twofold.valen.game.darkages.reader.model.*;
import be.twofold.valen.game.darkages.reader.packagemapspec.*;
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.darkages.reader.skeleton.*;
import be.twofold.valen.game.darkages.reader.strandshair.*;
import be.twofold.valen.game.darkages.reader.vegetation.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.hash.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DarkAgesGame implements Game {
    private static final DeclReader DECL_READER = new DeclReader();
    private static final List<AssetReader<?, DarkAgesAsset>> ASSET_READERS = List.of(
        DECL_READER,
        new BinaryFileReader(),
        new BinkReader(),
        new ImageReader(true),
        new MaterialReader(DECL_READER),
        new Md6AnimReader(),
        new Md6ModelReader(true),
        new Md6SkelReader(),
        new RenderParmReader(),
        new StaticModelReader(true),
        new StrandsHairReader(),
        new VegetationReader(true)
    );

    private final Path base;
    private final PackageMapSpec spec;
    private final StreamDbIndex streamDbIndex;
    private final ResourcesIndex commonResources;
    private final Decompressors decompressors;
    private final ContainerMask masks;

    DarkAgesGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressors = new Decompressors(null);
        this.masks = readMasks();
        this.streamDbIndex = loadStreamDbIndex(base, spec);
        this.commonResources = ResourcesIndex.build(filterResources(spec, "common", "warehouse", "init"), masks);
    }

    private ContainerMask readMasks() throws IOException {
        var metaResources = ResourcesIndex.build(List.of(base.resolve("meta.resources")), new ContainerMask(Map.of()));
        var metaArchive = Archive.of(metaResources.assets());
        var metaSources = metaResources.sources();
        try (var metaStorageManager = new StorageManager(metaSources, Set.of(), decompressors)) {
            var metaLoader = new AssetLoader(metaArchive, metaStorageManager, List.of());
            var asset = metaLoader.all()
                .filter(a -> a.id().fullName().equals("generated/buildgame/container.mask"))
                .findFirst()
                .orElseThrow(() -> new IOException("Could not find container.mask"));

            var decompressed = metaLoader.load(asset.id(), Bytes.class);
            return ContainerMask.read(BinarySource.wrap(decompressed));
        }
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .filter(map -> spec.mapFiles().get(map).stream()
                .anyMatch(file -> file.endsWith(".resources")))
            .toList();
    }

    public AssetLoader open(String name) throws IOException {
        var loadedResources = ResourcesIndex.build(filterResources(spec, name), masks);

        var archive = Archive.layered(
            Archive.of(loadedResources.assets()),
            Archive.of(commonResources.assets())
        );

        var sources = new HashMap<Path, BinarySource>();
        sources.putAll(streamDbIndex.sources());
        sources.putAll(commonResources.sources());
        sources.putAll(loadedResources.sources());
        var storageManager = new DarkAgesStorageManager(
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
