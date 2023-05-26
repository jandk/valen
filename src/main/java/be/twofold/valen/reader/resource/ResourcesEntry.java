package be.twofold.valen.reader.resource;

import be.twofold.valen.*;

import java.util.*;

public record ResourcesEntry(
    ResourcesName name,
    String type,
    int dependencyIndexNumber,
    int pathTupleIndex,
    int dataOffset,
    int dataSize,
    int dataSizeUncompressed,
    long dataCheckSum,
    long timestamp,
    long streamResourceHash,
    int version,
    int havokFlag1,
    short compressionMode,
    byte havokFlag2,
    byte havokFlag3,
    short numDependencies
) {
    static final int Size = 0x90;

    public static ResourcesEntry read(BetterBuffer buffer, int[] pathStringIndexes, List<String> strings) {
        buffer.expectLong(0);
        buffer.expectLong(1);
        buffer.expectLong(-1);
        int dependencyIndexNumber = buffer.getLongAsInt();
        int pathTupleIndex = buffer.getLongAsInt();
        buffer.expectLong(0);
        buffer.expectLong(0);
        int dataOffset = buffer.getLongAsInt();
        int dataSize = buffer.getLongAsInt();
        int dataSizeUncompressed = buffer.getLongAsInt();
        long dataCheckSum = buffer.getLong();
        long timestamp = buffer.getLong();
        long streamResourceHash = buffer.getLong();
        int version = buffer.getInt();
        int havokFlag1 = buffer.getInt();
        short compressionMode = buffer.getShort();
        byte havokFlag2 = buffer.getByte();
        byte havokFlag3 = buffer.getByte();
        buffer.expectInt(0); // padding
        buffer.expectInt(0); // padding
        buffer.expectInt(0); // flags
        buffer.expectInt(2); // desired compression mode
        short numDependencies = buffer.getShort();
        buffer.expectShort((short) 0);
        buffer.expectLong(0);

        String type = strings.get(pathStringIndexes[pathTupleIndex]);
        String name = strings.get(pathStringIndexes[pathTupleIndex + 1]);

        return new ResourcesEntry(
            ResourcesName.parse(name),
            type,
            dependencyIndexNumber,
            pathTupleIndex,
            dataOffset,
            dataSize,
            dataSizeUncompressed,
            dataCheckSum,
            timestamp,
            streamResourceHash,
            version,
            havokFlag1,
            compressionMode,
            havokFlag2,
            havokFlag3,
            numDependencies
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ResourcesEntry other)) return false;
        return name.equals(other.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
