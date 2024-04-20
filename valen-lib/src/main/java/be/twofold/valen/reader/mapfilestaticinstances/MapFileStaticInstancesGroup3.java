package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.util.*;

public record MapFileStaticInstancesGroup3(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesGroup3 read(BetterBuffer buffer) {
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        return new MapFileStaticInstancesGroup3(unknown1, unknown2);
    }
}
