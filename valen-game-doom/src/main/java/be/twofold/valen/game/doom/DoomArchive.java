package be.twofold.valen.game.doom;

import be.twofold.valen.core.compression.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.doom.readers.*;
import be.twofold.valen.game.doom.resources.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class DoomArchive implements Archive<DoomAssetID, DoomAsset> {
    private static final Decompressor INFLATE = Decompressor.inflate(true);
    private final AssetReaders<DoomAsset> assetReaders;
    private final Map<DoomAssetID, DoomAsset> index;
    private final DataSource source;

    DoomArchive(Path base, String name) throws IOException {
        this.source = DataSource.fromPath(base.resolve(name + ".resources"));
        this.index = ResourcesIndex
            .read(base.resolve(name + ".index"))
            .entries().stream()
            .map(this::toAsset)
            .collect(Collectors.toUnmodifiableMap(
                DoomAsset::id,
                Function.identity(),
                (first, second) -> first
            ));

        this.assetReaders = new AssetReaders<>(List.of(
            new BinaryFileReader()
        ));
    }

    private DoomAsset toAsset(ResourcesIndexEntry entry) {
        return new DoomAsset(
            new DoomAssetID(entry.resourceName()),
            AssetType.RAW,
            entry.offset(),
            entry.size(),
            entry.sizeCompressed(),
            entry.typeName()
        );
    }

    @Override
    public <T> T loadAsset(DoomAssetID identifier, Class<T> clazz) throws IOException {
        var asset = get(identifier).orElseThrow();
        source.position(asset.offset());
        ByteBuffer compressed = source.readBuffer(asset.sizeCompressed());
        ByteBuffer decompressed = compressed;

        if (asset.size() != asset.sizeCompressed()) {
            decompressed = INFLATE.decompress(compressed, asset.size());
        }

        return (T) assetReaders.read(asset, DataSource.fromBuffer(decompressed), ByteBuffer.class);
    }

    @Override
    public Optional<DoomAsset> get(DoomAssetID key) {
        return Optional.ofNullable(index.get(key));
    }

    @Override
    public Stream<DoomAsset> getAll() {
        return index.values().stream();
    }

    @Override
    public void close() throws IOException {
        source.close();
    }
}
