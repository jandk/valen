package be.twofold.valen.game.deathloop;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.game.deathloop.index.*;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public final class DeathloopArchive implements Archive {
    private static final Set<String> FILTER = Set.of("cpuimage", "image");

    private final Index index;
    private final List<DataSource> dataSources;
    private final Map<String, IndexEntry> indexEntries;

    public DeathloopArchive(Path indexFile, List<Path> dataFiles) throws IOException {
        this.index = Index.read(indexFile);
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
                IndexEntry::fileName,
                Function.identity()
            ));
    }

    @Override
    public List<Asset> assets() {
        return index.entries().stream()
            .filter(e -> !FILTER.contains(e.typeName()) || e.useBits() == 0)
            .map(this::mapIndexEntry)
            .toList();
    }

    private Asset mapIndexEntry(IndexEntry entry) {
        return new Asset(
            new DeathloopAssetId(entry.fileName()),
            AssetType.Binary,
            entry.uncompressedLength(),
            Map.of()
        );
    }

    @Override
    public boolean exists(AssetID identifier) {
        return indexEntries.containsKey(identifier.fullName());
    }

    @Override
    public Object loadAsset(AssetID identifier) throws IOException {
        return null;
    }

    @Override
    public ByteBuffer loadRawAsset(AssetID identifier) throws IOException {
        return null;
    }
}
