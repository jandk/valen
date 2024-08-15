package be.twofold.valen.game.colossus.reader.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourceHeader(
    int flags,
    int numSegments,
    long segmentSize,
    long metadataHash,
    int numResources,
    int numDependencies,
    int numDepIndices,
    int numStringIndices,
    int numSpecialHashes,
    int numMetaEntries,
    int stringTableSize,
    int metaEntriesSize,
    long stringTableOffset,
    long metaEntriesOffset,
    long resourceEntriesOffset,
    long resourceDepsOffset,
    long resourceSpecialHashOffset,
    long metaSize
) {
    public static ResourceHeader read(DataSource source) throws IOException {
        var flags = source.readInt();
        var numSegments = source.readInt();
        var segmentSize = source.readLong();
        var metadataHash = source.readLong();
        var numResources = source.readInt();
        var numDependencies = source.readInt();
        var numDepIndices = source.readInt();
        var numStringIndices = source.readInt();
        var numSpecialHashes = source.readInt();
        var numMetaEntries = source.readInt();
        var stringTableSize = source.readInt();
        var metaEntriesSize = source.readInt();
        var stringTableOffset = source.readLong();
        var metaEntriesOffset = source.readLong();
        var resourceEntriesOffset = source.readLong();
        var resourceDepsOffset = source.readLong();
        var resourceSpecialHashOffset = source.readLong();
        var metaSize = source.readLong();

        return new ResourceHeader(
            flags,
            numSegments,
            segmentSize,
            metadataHash,
            numResources,
            numDependencies,
            numDepIndices,
            numStringIndices,
            numSpecialHashes,
            numMetaEntries,
            stringTableSize,
            metaEntriesSize,
            stringTableOffset,
            metaEntriesOffset,
            resourceEntriesOffset,
            resourceDepsOffset,
            resourceSpecialHashOffset,
            metaSize
        );
    }
}
