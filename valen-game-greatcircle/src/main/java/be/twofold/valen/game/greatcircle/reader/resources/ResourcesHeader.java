package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesHeader(
    long headerHash,
    int numResources,
    int numDependencies,
    int numDepIndices,
    int numStringIndices,
    int numSpecialHashes,
    int stringTableSize,
    long stringTableOffset,
    long metaEntriesOffset,
    long resourceEntriesOffset,
    long resourceDepsOffset,
    long resourceSpecialHashOffset,
    long dataOffset,
    long metaSize,
    long metaHash
) {
    public static ResourcesHeader read(BinarySource source) throws IOException {
        source.expectInt(0x4C434449); // magic
        source.expectInt(0xD); // version
        var headerHash = source.readLong();
        source.expectInt(0x74); // headerSize
        source.expectInt(0x0); // flags
        source.expectInt(0x1); // numSegments
        source.expectLong(0xFFFFFFFFFFL); // segmentSize
        var numResources = source.readInt();
        var numDependencies = source.readInt();
        var numDepIndices = source.readInt();
        var numStringIndices = source.readInt();
        var numSpecialHashes = source.readInt();
        source.expectInt(0x0); // numMetaEntries
        var stringTableSize = source.readInt();
        source.expectInt(0x0); // metaEntriesSize
        var stringTableOffset = source.readLong();
        var metaEntriesOffset = source.readLong();
        var resourceEntriesOffset = source.readLong();
        var resourceDepsOffset = source.readLong();
        var resourceSpecialHashOffset = source.readLong();
        var dataOffset = source.readLong();
        source.expectInt(0x0); // unknown
        var metaSize = source.readLong();
        var metaHash = source.readLong();

        return new ResourcesHeader(
            headerHash,
            numResources,
            numDependencies,
            numDepIndices,
            numStringIndices,
            numSpecialHashes,
            stringTableSize,
            stringTableOffset,
            metaEntriesOffset,
            resourceEntriesOffset,
            resourceDepsOffset,
            resourceSpecialHashOffset,
            dataOffset,
            metaSize,
            metaHash
        );
    }
}
