package be.twofold.valen.game.eternal.reader.resource;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record ResourcesEntry(
    int depIndices,
    int strings,
    int dataOffset,
    int dataSize,
    int uncompressedSize,
    long dataCheckSum,
    long generationTimeStamp,
    long defaultHash,
    int version,
    int flags,
    ResourceCompressionMode compMode,
    short variation,
    short numDependencies
) {
    public static ResourcesEntry read(BinaryReader reader) throws IOException {
        reader.expectLong(0); // resourceTypeString
        reader.expectLong(1); // nameString
        reader.expectLong(-1); // descString
        var depIndices = reader.readLongAsInt();
        var strings = reader.readLongAsInt();
        reader.expectLong(0); // specialHashes
        reader.expectLong(0); // metaEntries
        var dataOffset = reader.readLongAsInt();
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
        reader.expectShort((short) 2); // numStrings
        // source.expectShort((short) 0); // numSources
        reader.readShort(); // numSources
        var numDependencies = reader.readShort();
        reader.expectShort((short) 0); // numSpecialHashes
        reader.expectShort((short) 0); // numMetaEntries
        reader.skip(6); // padding

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
            numDependencies
        );
    }
}
