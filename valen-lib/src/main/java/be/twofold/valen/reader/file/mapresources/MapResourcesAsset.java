package be.twofold.valen.reader.file.mapresources;

import be.twofold.valen.core.util.*;

public record MapResourcesAsset(
    int type,
    String name,
    int unknown1,
    int unknown2,
    int unknown3,
    int unknown4
) {
    public static MapResourcesAsset read(BetterBuffer buffer) {
        int type = Integer.reverseBytes(buffer.getInt());
        String name = buffer.getString();
        int unknown1 = Integer.reverseBytes(buffer.getInt());
        int unknown2 = Integer.reverseBytes(buffer.getInt());
        int unknown3 = Integer.reverseBytes(buffer.getInt());
        buffer.expectInt(0);
        buffer.expectInt(0);
        int unknown4 = Integer.reverseBytes(buffer.getInt());
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
