package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.util.*;

public record StaticInstanceGroup3(
    int unknown1,
    int unknown2
) {
    public static StaticInstanceGroup3 read(BetterBuffer buffer) {
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        return new StaticInstanceGroup3(unknown1, unknown2);
    }
}
