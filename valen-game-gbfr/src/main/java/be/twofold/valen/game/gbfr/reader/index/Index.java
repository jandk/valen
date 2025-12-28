package be.twofold.valen.game.gbfr.reader.index;

import be.twofold.valen.game.gbfr.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.hash.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public record Index(
    String archiveName,
    short numArchives,
    Longs archiveHashes,
    List<FileEntry> fileEntries,
    List<ChunkEntry> chunkEntries,
    Longs externalHashes,
    Longs externalSizes,
    Ints cachedChunks
) {
    public static Index load(Path path) throws IOException {
        try (var source = BinarySource.open(path)) {
            return read(source);
        }
    }

    public static Index read(BinarySource source) throws IOException {
        var table = FlatBufferTable.root(source);
        var archiveName = table.readString(source, 0);
        var numArchives = table.readShort(source, 1);
        var archiveHashes = table.readLongs(source, 3);
        var chunkIndexes = table.readObjects(source, 4, FileEntry::read);
        var chunkEntries = table.readObjects(source, 5, ChunkEntry::read);
        var externalHashes = table.readLongs(source, 6);
        var externalSizes = table.readLongs(source, 7);
        var cachedChunks = table.readInts(source, 8);

        return new Index(
            archiveName,
            numArchives,
            archiveHashes,
            chunkIndexes,
            chunkEntries,
            externalHashes,
            externalSizes,
            cachedChunks
        );
    }

    public OptionalInt getIndex(String name) {
        var hash = HashFunction.xxHash64(0).hash(name);
        var result = unsignedBinarySearch(archiveHashes, hash.asLong());
        return result < 0 ? OptionalInt.empty() : OptionalInt.of(result);
    }

    private int unsignedBinarySearch(Longs longs, long key) {
        int lo = 0;
        int hi = longs.length() - 1;
        key += Long.MIN_VALUE;

        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            long midVal = longs.get(mid) + Long.MIN_VALUE;

            if (midVal < key) {
                lo = mid + 1;
            } else if (midVal > key) {
                hi = mid - 1;
            } else {
                return mid;
            }
        }
        return -1;
    }


    @Override
    public String toString() {
        return "Index(" +
            "archiveName='" + archiveName + "', " +
            "numArchives=" + numArchives + ", " +
            "archiveHashes=" + archiveHashes + ", " +
            "chunkIndexes=[" + fileEntries.size() + " indexes], " +
            "chunkEntries=[" + chunkEntries.size() + " entries], " +
            "externalHashes=" + externalHashes + ", " +
            "externalSizes=" + externalSizes + ", " +
            "cachedChunks=" + cachedChunks +
            ")";
    }
}
