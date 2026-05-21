package be.twofold.valen.game.darkages.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesHeader(
    int numResources,
    int numDependencies,
    int numDepIndices,
    int numStringIndices,
    int stringTableSize,
    long stringTableOffset,
    long metaEntriesOffset,
    long resourceEntriesOffset,
    long resourceDepsOffset,
    long resourceSpecialHashOffset,
    long dataOffset
) {
    public static ResourcesHeader read(BinarySource source) throws IOException {
        source.expectInt(0x4C434449); // magic
        source.expectInt(0xD); // version
        source.expectInt(0x0); // flags
        source.expectInt(0x1); // numSegments
        source.expectLong(0xFFFFFFFFFFL); // segmentSize
        source.expectLong(0x0); // metadataHash
        var numResources = source.readInt();
        var numDependencies = source.readInt();
        var numDepIndices = source.readInt();
        var numStringIndices = source.readInt();
        source.expectInt(0x0); // numSpecialHashes
        source.expectInt(0x0); // numMetaEntries
        var stringTableSize = source.readInt();
        source.expectInt(0x0); // metaEntriesSize
        var stringTableOffset = source.readLong();
        var metaEntriesOffset = source.readLong();
        var resourceEntriesOffset = source.readLong();
        var resourceDepsOffset = source.readLong();
        var resourceSpecialHashOffset = source.readLong();
        var dataOffset = source.readLong();

        return new ResourcesHeader(
            numResources,
            numDependencies,
            numDepIndices,
            numStringIndices,
            stringTableSize,
            stringTableOffset,
            metaEntriesOffset,
            resourceEntriesOffset,
            resourceDepsOffset,
            resourceSpecialHashOffset,
            dataOffset
        );
    }
}
