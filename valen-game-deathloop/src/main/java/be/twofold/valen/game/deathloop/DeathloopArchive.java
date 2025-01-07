package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.deathloop.image.*;
import be.twofold.valen.game.deathloop.index.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class DeathloopArchive implements Archive {
    private static final Set<String> FILTER = Set.of("cpuimage", "image");

    private final Index index;
    private final Decompressor decompressor;

    private final List<DataSource> dataSources;
    private final Map<AssetID, IndexEntry> indexEntries;

    public DeathloopArchive(Path indexFile, List<Path> dataFiles, Decompressor decompressor) throws IOException {
        this.index = Index.read(indexFile);
        this.decompressor = Check.notNull(decompressor);

        var dataSources = new ArrayList<DataSource>(dataFiles.size());
        for (var dataFile : dataFiles) {
            dataSources.add(DataSource.fromPath(dataFile));
        }
        this.dataSources = List.copyOf(dataSources);

        // Some filenames are duplicates, but analysis shows they point to the same data
        // So we can just filter out the non-unique filenames and be done with it
        var unique = new HashSet<String>();
        this.indexEntries = index.entries().stream()
            .filter(e -> unique.add(e.fileName()))
            .collect(Collectors.toUnmodifiableMap(
                this::mapToAssetID,
                Function.identity()
            ));
    }

    @Override
    public List<Asset> assets() {
        return index.entries().stream()
            // .filter(e -> !FILTER.contains(e.typeName()) || e.fileName().endsWith(".bimage"))
            .map(this::mapIndexEntry)
            .toList();
    }

    private Asset mapIndexEntry(IndexEntry entry) {
        return new Asset(
            mapToAssetID(entry),
            mapAssetType(entry),
            entry.uncompressedLength(),
            Map.of("Type", entry.typeName())
        );
    }

    private DeathloopAssetID mapToAssetID(IndexEntry entry) {
        return new DeathloopAssetID(entry.fileName());
    }

    private AssetType<?> mapAssetType(IndexEntry entry) {
        switch (entry.typeName()) {
            case "image":
                return AssetType.TEXTURE;
            default:
                return AssetType.BINARY;
        }
    }

    @Override
    public boolean exists(AssetID identifier) {
        return indexEntries.containsKey(identifier);
    }

    @Override
    public <T> T loadAsset(AssetID identifier, Class<T> clazz) throws IOException {
        var entry = indexEntries.get(identifier);

        var source = dataSources.get(entry.fileId());
        source.seek(entry.offset());

        var bytes = source.readBytes(entry.compressedLength());
        if (entry.compressedLength() != entry.uncompressedLength()) {
            var uncompressed = new byte[entry.uncompressedLength()];
            decompressor.decompress(
                bytes, 12, bytes.length - 12,
                uncompressed, 0, uncompressed.length
            );
            bytes = uncompressed;
        }

        if (clazz == byte[].class) {
            return (T) bytes;
        }

        switch (entry.typeName()) {
            case "image":
                return (T) new ImageReader(this).read(DataSource.fromArray(bytes), entry);
            default:
                throw new IllegalArgumentException("Unsupported asset type: " + entry.typeName());
        }
    }
}
