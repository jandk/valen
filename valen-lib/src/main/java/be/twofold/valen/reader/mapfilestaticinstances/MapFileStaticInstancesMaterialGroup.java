package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.util.*;

public record MapFileStaticInstancesMaterialGroup(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesMaterialGroup read(BetterBuffer buffer) {
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        return new MapFileStaticInstancesMaterialGroup(unknown1, unknown2);
    }
}
