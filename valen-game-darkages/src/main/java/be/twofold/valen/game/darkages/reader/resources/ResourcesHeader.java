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
    public static ResourcesHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x4C434449); // magic
        reader.expectInt(0xD); // version
        reader.expectInt(0x0); // flags
        reader.expectInt(0x1); // numSegments
        reader.expectLong(0xFFFFFFFFFFL); // segmentSize
        reader.expectLong(0x0); // metadataHash
        var numResources = reader.readInt();
        var numDependencies = reader.readInt();
        var numDepIndices = reader.readInt();
        var numStringIndices = reader.readInt();
        reader.expectInt(0x0); // numSpecialHashes
        reader.expectInt(0x0); // numMetaEntries
        var stringTableSize = reader.readInt();
        reader.expectInt(0x0); // metaEntriesSize
        var stringTableOffset = reader.readLong();
        var metaEntriesOffset = reader.readLong();
        var resourceEntriesOffset = reader.readLong();
        var resourceDepsOffset = reader.readLong();
        var resourceSpecialHashOffset = reader.readLong();
        var dataOffset = reader.readLong();

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
