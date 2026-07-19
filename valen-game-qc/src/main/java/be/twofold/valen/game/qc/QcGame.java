package be.twofold.valen.game.qc;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.qc.reader.pak.*;
import be.twofold.valen.game.qc.reader.pct.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class QcGame implements Game {
    private static final List<AssetReader<?, ?>> READERS = List.of(
        new PctReader()
    );

    private final Path paksPath;
    private final List<String> archiveNames;

    public QcGame(Path clientPath) throws IOException {
        this.paksPath = clientPath.resolve("preload", "paks");

        try (var stream = Files.list(paksPath)) {
            this.archiveNames = stream
                .filter(p -> p.getFileName().toString().endsWith(".pak"))
                .map(p -> Filenames.getBaseName(p.getFileName().toString()))
                .sorted()
                .toList();
        }
    }

    @Override
    public List<String> archiveNames() {
        return archiveNames;
    }

    @Override
    public AssetLoader open(String name) throws IOException {
        Path resolved = paksPath.resolve(name + ".pak");
        var source = BinarySource.open(resolved);

        Pak pak = Pak.read(source);
        var assets = pak.entries().stream()
            .map(entry -> mapEntry(entry, resolved))
            .toList();

        var archive = Archive.of(assets);
        var storageManager = new StorageManager(Map.of(resolved, source), Set.of(), new Decompressors(null));

        return new AssetLoader(archive, storageManager, READERS);
    }

    private QcAsset mapEntry(PakEntry entry, Path path) {
        Location location = new Location.FileSlice(path,
            entry.relativeOffsetOfLocalHeader() + 30,
            Math.toIntExact(entry.compressedSize()));

        if (entry.compressionMethod() == 0) {
            // Do nothing
        } else if (entry.compressionMethod() == 8) {
            int size = Math.toIntExact(entry.uncompressedSize());
            location = new Location.Compressed(location, CompressionType.DEFLATE_RAW, size);
        } else {
            throw new UnsupportedOperationException("Unsupported compression method: " + entry.compressionMethod());
        }

        return new QcAsset(new QcAssetId(entry.fileName()/*.replace('\\', '/')*/), location);
    }
}
