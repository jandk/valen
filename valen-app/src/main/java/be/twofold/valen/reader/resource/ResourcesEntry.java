package be.twofold.valen.reader.resource;

import be.twofold.valen.core.util.*;

public record ResourcesEntry(
    ResourcesName name,
    String type,
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
    static final int Size = 0x90;

    public static ResourcesEntry read(BetterBuffer buffer, String[] pathStrings, int[] pathStringIndexes) {
        buffer.expectLong(0); // resourceTypeString
        buffer.expectLong(1); // nameString
        buffer.expectLong(-1); // descString
        int depIndices = buffer.getLongAsInt();
        int strings = buffer.getLongAsInt();
        buffer.expectLong(0); // specialHashes
        buffer.expectLong(0); // metaEntries
        int dataOffset = buffer.getLongAsInt();
        int dataSize = buffer.getLongAsInt();
        int uncompressedSize = buffer.getLongAsInt();
        long dataCheckSum = buffer.getLong();
        long generationTimeStamp = buffer.getLong();
        long defaultHash = buffer.getLong();
        int version = buffer.getInt();
        int flags = buffer.getInt();
        byte compMode = buffer.getByte();
        buffer.expectByte(0); // reserved0
        short variation = buffer.getShort();
        buffer.expectInt(0); // reserved2
        buffer.expectLong(0); // reservedForVariations
        buffer.expectShort(2); // numStrings
        buffer.expectShort(0); // numSources
        short numDependencies = buffer.getShort();
        buffer.expectShort(0); // numSpecialHashes
        buffer.expectShort(0); // numMetaEntries
        buffer.skip(6); // padding

        String type = pathStrings[pathStringIndexes[strings]];
        String name = pathStrings[pathStringIndexes[strings + 1]];

        return new ResourcesEntry(
            ResourcesName.parse(name),
            type,
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

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ResourcesEntry other
            && name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
