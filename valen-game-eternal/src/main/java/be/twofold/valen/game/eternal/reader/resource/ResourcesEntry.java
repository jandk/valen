package be.twofold.valen.game.eternal.reader.resource;

import be.twofold.valen.core.io.*;

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
    public static final int BYTES = 144;

    public static ResourcesEntry read(DataSource source) throws IOException {
        source.expectLong(0); // resourceTypeString
        source.expectLong(1); // nameString
        source.expectLong(-1); // descString
        var depIndices = source.readLongAsInt();
        var strings = source.readLongAsInt();
        source.expectLong(0); // specialHashes
        source.expectLong(0); // metaEntries
        var dataOffset = source.readLongAsInt();
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
        source.expectShort((short) 2); // numStrings
        source.expectShort((short) 0); // numSources
        var numDependencies = source.readShort();
        source.expectShort((short) 0); // numSpecialHashes
        source.expectShort((short) 0); // numMetaEntries
        source.skip(6); // padding

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
