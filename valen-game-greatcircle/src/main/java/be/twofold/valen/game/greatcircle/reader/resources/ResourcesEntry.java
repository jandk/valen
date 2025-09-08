package be.twofold.valen.game.greatcircle.reader.resources;

import be.twofold.valen.core.io.*;

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
    public static ResourcesEntry read(BinaryReader reader) throws IOException {
        var resourceTypeString = reader.readLongAsInt();
        var nameString = reader.readLongAsInt();
        reader.expectLong(-1); // descString
        var depIndices = reader.readLongAsInt();
        var strings = reader.readLongAsInt();
        long specialHashes = reader.readLong(); // specialHashes
        reader.expectLong(0); // metaEntries
        var dataOffset = reader.readLong();
        var dataSize = reader.readLongAsInt();
        var uncompressedSize = reader.readLongAsInt();
        var dataCheckSum = reader.readLong();
        var generationTimeStamp = reader.readLong();
        var defaultHash = reader.readLong();
        var version = reader.readInt();
        var flags = reader.readInt();
        var compMode = ResourceCompressionMode.fromValue((int) reader.readByte());
        reader.expectByte((byte) 0); // reserved0
        var variation = reader.readShort();
        reader.expectInt(0); // reserved2
        reader.expectLong(0); // reservedForVariations
        var numStrings = reader.readShort();
        reader.expectShort((short) 0); // numSources
        var numDependencies = reader.readShort();
        var numSpecialHashes = reader.readShort();
        reader.expectShort((short) 0); // numMetaEntries
        reader.skip(6); // padding

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
