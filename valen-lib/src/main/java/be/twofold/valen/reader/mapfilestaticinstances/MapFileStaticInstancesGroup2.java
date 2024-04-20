package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.util.*;

public record MapFileStaticInstancesGroup2(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesGroup2 read(BetterBuffer buffer) {
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        return new MapFileStaticInstancesGroup2(unknown1, unknown2);
    }
}
