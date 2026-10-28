package be.twofold.valen.game.colossus.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourceEntry(
    int depIndices,
    int strings,
    long dataOffset,
    int dataSize,
    long uncompressedSize,
    long dataCheckSum,
    long generationTimeStamp,
    long defaultHash,
    int version,
    int flags,
    ResourceCompressionMode compMode,
    ResourcesVariation variation,
    short numStrings,
    short numDependencies
) {
    public static ResourceEntry read(BinarySource source) throws IOException {
        source.expectLong(0); // resourceTypeString
        source.expectLong(1); // nameString
        source.expectLong(-1); // descString
        var depIndices = source.readLongAsInt();
        var strings = source.readLongAsInt();
        source.expectLong(0); // specialHashes
        source.expectLong(0); // metaEntries
        var dataOffset = source.readLong();
        var dataSize = source.readLongAsInt();
        var uncompressedSize = source.readLong();
        var dataCheckSum = source.readLong();
        var generationTimeStamp = source.readLong();
        var defaultHash = source.readLong();
        var version = source.readInt();
        var flags = source.readInt();
        var compMode = ResourceCompressionMode.fromValue(source.readByte());
        source.expectByte((byte) 0); // reserved0
        var variation = ResourcesVariation.read(source);
        source.expectInt(0); // reserved2
        source.expectLong(0); // reservedForVariations
        var numStrings = source.readShort();
        source.expectShort((short) 0); // numSources
        var numDependencies = source.readShort();
        source.expectShort((short) 0); // numSpecialHashes
        source.expectShort((short) 0); // numMetaEntries
        source.skip(6);

        return new ResourceEntry(
            depIndices,
            strings,
            dataOffset,
            dataSize,
            uncompressedSize,
            dataCheckSum,
            generationTimeStamp,
            defaultHash,
            version,
            flags,
            compMode,
            variation,
            numStrings,
            numDependencies
        );
    }
}
