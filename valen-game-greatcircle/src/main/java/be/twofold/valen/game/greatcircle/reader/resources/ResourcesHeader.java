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
    public static ResourcesHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x4C434449); // magic
        reader.expectInt(0xD); // version
        var headerHash = reader.readLong();
        reader.expectInt(0x74); // headerSize
        reader.expectInt(0x0); // flags
        reader.expectInt(0x1); // numSegments
        reader.expectLong(0xFFFFFFFFFFL); // segmentSize
        var numResources = reader.readInt();
        var numDependencies = reader.readInt();
        var numDepIndices = reader.readInt();
        var numStringIndices = reader.readInt();
        var numSpecialHashes = reader.readInt();
        reader.expectInt(0x0); // numMetaEntries
        var stringTableSize = reader.readInt();
        reader.expectInt(0x0); // metaEntriesSize
        var stringTableOffset = reader.readLong();
        var metaEntriesOffset = reader.readLong();
        var resourceEntriesOffset = reader.readLong();
        var resourceDepsOffset = reader.readLong();
        var resourceSpecialHashOffset = reader.readLong();
        var dataOffset = reader.readLong();
        reader.expectInt(0x0); // unknown
        var metaSize = reader.readLong();
        var metaHash = reader.readLong();

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
