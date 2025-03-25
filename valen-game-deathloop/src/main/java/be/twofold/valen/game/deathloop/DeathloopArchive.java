package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.deathloop.image.*;
import be.twofold.valen.game.deathloop.index.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class DeathloopArchive implements Archive<DeathloopAssetID, DeathloopAsset> {
    private static final Set<String> FILTER = Set.of("cpuimage", "image");

    private final Decompressor decompressor;

    private final List<DataSource> dataSources;
    private final Map<DeathloopAssetID, DeathloopAsset> assetIndex;
    private final AssetReaders<DeathloopAsset> assetReaders;

    public DeathloopArchive(Path indexFile, List<Path> dataFiles, Decompressor decompressor) throws IOException {
        var index = Index.read(indexFile);
        this.decompressor = Check.notNull(decompressor);

        var dataSources = new ArrayList<DataSource>(dataFiles.size());
        for (var dataFile : dataFiles) {
            dataSources.add(DataSource.fromPath(dataFile));
        }
        this.dataSources = List.copyOf(dataSources);

        // Some filenames are duplicates, but analysis shows they point to the same data
        // So we can just filter out the non-unique filenames and be done with it
        var unique = new HashSet<String>();
        this.assetIndex = index.entries().stream()
            .filter(e -> unique.add(e.fileName()))
            .map(entry -> new DeathloopAsset(new DeathloopAssetID(entry.fileName()), entry))
            .collect(Collectors.toUnmodifiableMap(
                DeathloopAsset::id,
                Function.identity()
            ));

        this.assetReaders = new AssetReaders<>(List.of(
            new ImageReader(this)
        ));
    }

    @Override
    public Optional<DeathloopAsset> get(DeathloopAssetID identifier) {
        return Optional.ofNullable(assetIndex.get(identifier));
    }

    @Override
    public Stream<DeathloopAsset> getAll() {
        return assetIndex.values().stream();
    }

    @Override
    public <T> T loadAsset(DeathloopAssetID identifier, Class<T> clazz) throws IOException {
        var asset = assetIndex.get(identifier);
        var entry = asset.entry();

        var source = dataSources.get(entry.fileId());
        source.position(entry.offset());

        var compressed = source.readBuffer(entry.compressedLength()).position(12);
        if (entry.compressedLength() != entry.uncompressedLength()) {
            var uncompressed = ByteBuffer.allocate(entry.uncompressedLength());
            decompressor.decompress(
                compressed.position(12),
                uncompressed
            );
            compressed = uncompressed.flip();
        }

        return assetReaders.read(asset, DataSource.fromBuffer(compressed), clazz);
    }

    @Override
    public void close() throws IOException {
        for (var dataSource : dataSources) {
            dataSource.close();
        }
    }
}
