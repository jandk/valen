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
import be.twofold.valen.game.darkages.reader.resources.*;
import be.twofold.valen.game.darkages.reader.skeleton.*;
import be.twofold.valen.game.darkages.reader.strandshair.*;
import be.twofold.valen.game.darkages.reader.streamdb.*;
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
    private final List<StreamDbFile> streamDbFiles;

    DarkAgesGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle(OodleDownloader.download());
        this.streamDbFiles = loadStreamDbFiles(base, spec, decompressor);
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .filter(map -> spec.mapFiles().get(map).stream()
                .anyMatch(file -> file.endsWith(".resources")))
            .toList();
    }

    public AssetLoader open(String name) throws IOException {
        var paths = filterResources(base, spec, name);
        var sources = new HashMap<FileId, BinarySource>();
        var resourceFiles = new ArrayList<ResourcesFile>();
        for (Path path : paths) {
            // TODO: Cleanup relativize
            var fileId = new FileId(base.relativize(path).toString());
            var source = BinarySource.open(path);
            sources.put(fileId, source);
            resourceFiles.add(new ResourcesFile(path, fileId));
        }

        var archive = new DarkAgesArchive(resourceFiles, List.of());
        var storageManager = new DarkAgesStorageManager(
            sources,
            streamDbFiles,
            decompressor
        );

        return new AssetLoader(archive, storageManager, ASSET_READERS);
    }

    private List<StreamDbFile> loadStreamDbFiles(Path base, PackageMapSpec spec, Decompressor decompressor) throws IOException {
        var paths = spec.files().stream()
            .filter(s -> s.endsWith(".streamdb"))
            .map(base::resolve)
            .toList();

        var files = new ArrayList<StreamDbFile>();
        for (var path : paths) {
            files.add(new StreamDbFile(path, decompressor));
        }
        return files;
    }

    private List<Path> filterResources(Path base, PackageMapSpec spec, String... names) {
        return Arrays.stream(names)
            .flatMap(map -> spec.mapFiles().get(map).stream())
            .filter(file -> file.endsWith(".resources"))
            .map(base::resolve)
            .toList();
    }
}
