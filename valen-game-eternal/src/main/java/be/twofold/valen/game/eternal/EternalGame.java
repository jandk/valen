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
import be.twofold.valen.game.eternal.resource.*;
import be.twofold.valen.game.eternal.stream.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class EternalGame implements Game {
    private final Path base;
    private final PackageMapSpec spec;
    private final Decompressor decompressor;
    private final List<StreamDbFile> streamDbFiles;

    EternalGame(Path path) throws IOException {
        this.base = path.resolve("base");
        this.spec = PackageMapSpecReader.read(base.resolve("packagemapspec.json"));
        this.decompressor = Decompressor.oodle(path.resolve("oo2core_8_win64.dll"));
        this.streamDbFiles = loadStreamDbFiles(base, spec, decompressor);
    }

    @Override
    public List<String> archiveNames() {
        return spec.maps().stream()
            .filter(map -> spec.mapFiles().get(map).stream()
                .anyMatch(file -> file.endsWith(".resources")))
            .map(s -> s.equals("common") ? "gameresources" : s)
            .toList();
    }

    public AssetLoader open(String name) throws IOException {
        if (name.equals("gameresources")) {
            name = "common";
        }

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

        var archive = new EternalArchive(resourceFiles, List.of());
        var storageManager = new EternalStorageManager(
            sources,
            streamDbFiles,
            decompressor
        );

        var declReader = new DeclReader();
        List<AssetReader<?, EternalAsset>> assetReaders = List.of(
            declReader,
            // Binary converters
            new BinaryFileReader(),
            new FileCompressedReader(decompressor),
            new FileReader(),
            new JsonReader(),

            // Actual readers
            new ImageReader(),
            new MapFileStaticInstancesReader(),
            new MaterialReader(declReader),
            new Md6AnimReader(),
            new Md6ModelReader(),
            new Md6SkelReader(),
            new RenderParmReader(),
            new StaticModelReader()
        );
        // TODO: Fix this cast
        var assetReaderRegistry = new AssetReaders((List) assetReaders);

        return new AssetLoader(archive, storageManager, assetReaderRegistry);
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
