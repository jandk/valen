package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesEntry(
    int resourceTypeString,
    int nameString,
    int depIndices,
    int strings,
    long specialHashes,
    long dataOffset,
    int dataSize,
    int uncompressedSize,
    long dataCheckSum,
    long generationTimeStamp,
    long defaultHash,
    int version,
    int flags,
    ResourceCompressionMode compMode,
    short variation,
    short numStrings,
    short numDependencies,
    short numSpecialHashes
) {
    public static ResourcesEntry read(BinarySource source) throws IOException {
        var resourceTypeString = source.readLongAsInt();
        var nameString = source.readLongAsInt();
        source.expectLong(-1); // descString
        var depIndices = source.readLongAsInt();
        var strings = source.readLongAsInt();
        long specialHashes = source.readLong(); // specialHashes
        source.expectLong(0); // metaEntries
        var dataOffset = source.readLong();
        var dataSize = source.readLongAsInt();
        var uncompressedSize = source.readLongAsInt();
        var dataCheckSum = source.readLong();
        var generationTimeStamp = source.readLong();
        var defaultHash = source.readLong();
        var version = source.readInt();
        var flags = source.readInt();
        var compMode = ResourceCompressionMode.fromValue((int) source.readByte());
        source.expectByte((byte) 0); // reserved0
        var variation = source.readShort();
        source.expectInt(0); // reserved2
        source.expectLong(0); // reservedForVariations
        var numStrings = source.readShort();
        source.expectShort((short) 0); // numSources
        var numDependencies = source.readShort();
        var numSpecialHashes = source.readShort();
        source.expectShort((short) 0); // numMetaEntries
        source.skip(6); // padding

        return new ResourcesEntry(
            resourceTypeString,
            nameString,
            depIndices,
            strings,
            specialHashes,
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
            numDependencies,
            numSpecialHashes
        );
    }
}
