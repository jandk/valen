package be.twofold.valen.game.darkages;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.darkages.reader.anim.*;
import be.twofold.valen.game.darkages.reader.basemodel.*;
import be.twofold.valen.game.darkages.reader.binaryfile.*;
import be.twofold.valen.game.darkages.reader.bink.*;
import be.twofold.valen.game.darkages.reader.decl.*;
import be.twofold.valen.game.darkages.reader.decl.material2.*;
import be.twofold.valen.game.darkages.reader.decl.renderparm.*;
import be.twofold.valen.game.darkages.reader.image.*;
import be.twofold.valen.game.darkages.reader.model.*;
import be.twofold.valen.game.darkages.reader.packagemapspec.*;
import be.twofold.valen.game.darkages.reader.skeleton.*;
import be.twofold.valen.game.darkages.reader.strandshair.*;
import be.twofold.valen.game.darkages.reader.vegetation.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class DarkAgesGame implements Game {
    private static final AssetReaders ASSET_READERS;

    static {
        var declReader = new DeclReader();
        ASSET_READERS = new AssetReaders(List.of(
            declReader,
            new BinaryFileReader(),
            new BinkReader(),
            new ImageReader(true),
            new MaterialReader(declReader),
            new Md6AnimReader(),
            new Md6ModelReader(true),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader(true),
            new StrandsHairReader(),
            new VegetationReader(true)
        ));
    }

    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final StreamDbIndex streamDbIndex;
    private final ResourcesIndex commonResources;

    DarkAgesGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle(OodleDownloader.download());
        this.streamDbIndex = loadStreamDbIndex(base, spec);
        this.commonResources = ResourcesIndex.build(base, filterResources(spec, "common", "warehouse", "init"));
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

        var archive = new DarkAgesArchive(
            Map.copyOf(commonResources.index()),
            Map.copyOf(loadedResources.index())
        );

        var sources = new HashMap<FileId, BinarySource>();
        sources.putAll(streamDbIndex.sources());
        sources.putAll(commonResources.sources());
        sources.putAll(loadedResources.sources());
        var storageManager = new DarkAgesStorageManager(
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
