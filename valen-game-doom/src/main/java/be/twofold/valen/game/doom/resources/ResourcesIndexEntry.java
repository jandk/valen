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
    public static ResourcesIndexEntry read(BinaryReader reader) throws IOException {
        reader.order(ByteOrder.BIG_ENDIAN);
        var index = reader.readInt();

        reader.order(ByteOrder.LITTLE_ENDIAN);
        var typeName = reader.readString(StringFormat.INT_LENGTH);
        var resourceName = reader.readString(StringFormat.INT_LENGTH);
        var fileName = reader.readString(StringFormat.INT_LENGTH);

        reader.order(ByteOrder.BIG_ENDIAN);
        var offset = reader.readLong();
        var size = reader.readInt();
        var sizeCompressed = reader.readInt();

        reader.order(ByteOrder.LITTLE_ENDIAN);
        var unknown = reader.readInt();
        var fileId = reader.readByte();

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
