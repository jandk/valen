package be.twofold.valen.reader.resource;

import be.twofold.valen.core.util.*;

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
    byte compMode,
    short variation,
    short numDependencies
) {
    public static final int BYTES = 144;

    public static ResourcesEntry read(BetterBuffer buffer) {
        buffer.expectLong(0); // resourceTypeString
        buffer.expectLong(1); // nameString
        buffer.expectLong(-1); // descString
        var depIndices = buffer.getLongAsInt();
        var strings = buffer.getLongAsInt();
        buffer.expectLong(0); // specialHashes
        buffer.expectLong(0); // metaEntries
        var dataOffset = buffer.getLongAsInt();
        var dataSize = buffer.getLongAsInt();
        var uncompressedSize = buffer.getLongAsInt();
        var dataCheckSum = buffer.getLong();
        var generationTimeStamp = buffer.getLong();
        var defaultHash = buffer.getLong();
        var version = buffer.getInt();
        var flags = buffer.getInt();
        var compMode = buffer.getByte();
        buffer.expectByte(0); // reserved0
        var variation = buffer.getShort();
        buffer.expectInt(0); // reserved2
        buffer.expectLong(0); // reservedForVariations
        buffer.expectShort(2); // numStrings
        buffer.expectShort(0); // numSources
        var numDependencies = buffer.getShort();
        buffer.expectShort(0); // numSpecialHashes
        buffer.expectShort(0); // numMetaEntries
        buffer.skip(6); // padding

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
