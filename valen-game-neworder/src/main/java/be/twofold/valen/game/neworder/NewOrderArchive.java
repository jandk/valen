package be.twofold.valen.game.neworder;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.neworder.index.*;
import be.twofold.valen.game.neworder.master.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

public final class NewOrderArchive implements Archive {
    private final DataSource source;
    private final Map<NewOrderAssetID, List<IndexEntry>> entries;

    public NewOrderArchive(Path base, MasterContainer container) throws IOException {
        this.source = DataSource.fromPath(base.resolve(container.resourceName()));

        var index = Index.read(base.resolve(container.indexName()));
        this.entries = index.entries().stream()
            .collect(Collectors.groupingBy(e -> new NewOrderAssetID(e.typeName(), e.resourceName())));
    }

    @Override
    public List<Asset> assets() {
        return entries.entrySet().stream()
            .map(e -> mapEntry(e.getKey(), e.getValue()))
            .sorted()
            .toList();
    }

    private Asset mapEntry(NewOrderAssetID id, List<IndexEntry> entries) {
        return new Asset(
            id,
            AssetType.Binary,
            entries.stream().mapToInt(IndexEntry::uncompressedLength).sum(),
            Map.of()
        );
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean exists(AssetID identifier) {
        return entries.containsKey(identifier);
    }

    @Override
    public Object loadAsset(AssetID identifier) {
        return null;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) {
        return null;
    }
}
