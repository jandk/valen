package be.twofold.valen.game.doom.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record ResourcesIndexEntry(
    int index,
    String typeName,
    String resourceName,
    String fileName,
    long offset,
    int size,
    int sizeCompressed,
    int unknown,
    byte fileId
) {
    public static ResourcesIndexEntry read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);
        var index = source.readInt();

        source.order(ByteOrder.LITTLE_ENDIAN);
        var typeName = source.readString(StringFormat.INT_LENGTH);
        var resourceName = source.readString(StringFormat.INT_LENGTH);
        var fileName = source.readString(StringFormat.INT_LENGTH);

        source.order(ByteOrder.BIG_ENDIAN);
        var offset = source.readLong();
        var size = source.readInt();
        var sizeCompressed = source.readInt();

        source.order(ByteOrder.LITTLE_ENDIAN);
        var unknown = source.readInt();
        var fileId = source.readByte();

        return new ResourcesIndexEntry(
            index,
            typeName,
            resourceName,
            fileName,
            offset,
            size,
            sizeCompressed,
            unknown,
            fileId
        );
    }
}
