package be.twofold.valen.game.eternal.reader.file.mapresources;

import be.twofold.valen.core.io.*;

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
    public static MapResourcesAsset read(BinarySource source) throws IOException {
        int type = source.order(ByteOrder.BIG_ENDIAN).readInt();
        String name = source.order(ByteOrder.LITTLE_ENDIAN).readString(StringFormat.INT_LENGTH);
        int unknown1 = source.order(ByteOrder.BIG_ENDIAN).readInt();
        int unknown2 = source.readInt();
        int unknown3 = source.readInt();
        source.expectInt(0);
        source.expectInt(0);
        int unknown4 = source.readInt();
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
