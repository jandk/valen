package be.twofold.valen.game.eternal.reader.resource;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesEntry(
    long depIndices,
    long strings,
    long dataOffset,
    long dataSize,
    long uncompressedSize,
    long dataCheckSum,
    long generationTimeStamp,
    long defaultHash,
    int version,
    int flags,
    ResourceCompressionMode compMode,
    short variation,
    short numSources,
    short numDependencies
) {
    public static ResourcesEntry read(BinarySource source) throws IOException {
        source.expectLong(0x0); // resourceTypeString
        source.expectLong(0x1); // nameString
        source.expectLong(-0x1); // descString
        var depIndices = source.readLong();
        var strings = source.readLong();
        source.expectLong(0x0); // specialHashes
        source.expectLong(0x0); // metaEntries
        var dataOffset = source.readLong();
        var dataSize = source.readLong();
        var uncompressedSize = source.readLong();
        var dataCheckSum = source.readLong();
        var generationTimeStamp = source.readLong();
        var defaultHash = source.readLong();
        var version = source.readInt();
        var flags = source.readInt();
        var compMode = ResourceCompressionMode.read(source);
        source.expectByte((byte) 0x0); // reserved0
        var variation = source.readShort();
        source.expectInt(0x0); // reserved2
        source.expectLong(0x0); // reservedForVariations
        source.expectShort((short) 0x2); // numStrings
        var numSources = source.readShort();
        var numDependencies = source.readShort();
        source.expectShort((short) 0x0); // numSpecialHashes
        source.expectShort((short) 0x0); // numMetaEntries
        source.expectInt(0x0); // padding1
        source.expectShort((short) 0x0); // padding2

        return new ResourcesEntry(
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
            numSources,
            numDependencies
        );
    }
}
