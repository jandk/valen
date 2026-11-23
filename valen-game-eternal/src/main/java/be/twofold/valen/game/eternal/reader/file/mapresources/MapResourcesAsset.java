package be.twofold.valen.game.eternal.reader.file.mapresources;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;

public record MapResourcesAsset(
    int type,
    String name,
    int unknown1,
    int unknown2,
    int unknown3,
    int unknown4
) {
    public static MapResourcesAsset read(BinaryReader reader) throws IOException {
        int type = reader.order(ByteOrder.BIG_ENDIAN).readInt();
        String name = reader.order(ByteOrder.LITTLE_ENDIAN).readString(StringFormat.INT_LENGTH);
        int unknown1 = reader.order(ByteOrder.BIG_ENDIAN).readInt();
        int unknown2 = reader.readInt();
        int unknown3 = reader.readInt();
        reader.expectZeros(8);
        int unknown4 = reader.readInt();
        return new MapResourcesAsset(
            type,
            name,
            unknown1,
            unknown2,
            unknown3,
            unknown4
        );
    }
}
