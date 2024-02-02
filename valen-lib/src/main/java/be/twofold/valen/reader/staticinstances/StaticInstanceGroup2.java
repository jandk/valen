package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.util.*;

public record StaticInstanceGroup2(
    int unknown1,
    int unknown2
) {
    public static StaticInstanceGroup2 read(BetterBuffer buffer) {
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        return new StaticInstanceGroup2(unknown1, unknown2);
    }
}
