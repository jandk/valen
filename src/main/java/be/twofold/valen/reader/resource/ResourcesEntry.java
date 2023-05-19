package be.twofold.valen.reader.resource;

import java.nio.*;

public record ResourcesEntry(
    long dependencyIndexNumber,
    long pathTupleIndex,
    long dataOffset,
    long dataSize,
    long dataSizeUncompressed,
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

    public static ResourcesEntry read(ByteBuffer buffer) {
        long dependencyIndexNumber = buffer.getLong(0x18);
        long pathTupleIndex = buffer.getLong(0x20);
        long dataOffset = buffer.getLong(0x38);
        long dataSize = buffer.getLong(0x40);
        long dataSizeUncompressed = buffer.getLong(0x48);
        long dataCheckSum = buffer.getLong(0x50);
        long timestamp = buffer.getLong(0x58);
        long streamResourceHash = buffer.getLong(0x60);
        int version = buffer.getInt(0x68);
        int havokFlag1 = buffer.getInt(0x6c);
        short compressionMode = buffer.getShort(0x70);
        byte havokFlag2 = buffer.get(0x72);
        byte havokFlag3 = buffer.get(0x73);
        short numDependencies = buffer.getShort(0x84);

        return new ResourcesEntry(
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
}
