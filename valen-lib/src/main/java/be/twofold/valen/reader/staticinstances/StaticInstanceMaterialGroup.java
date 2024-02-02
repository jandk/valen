package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.util.*;

public record StaticInstanceMaterialGroup(
    int unknown1,
    int unknown2
) {
    public static StaticInstanceMaterialGroup read(BetterBuffer buffer) {
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        return new StaticInstanceMaterialGroup(unknown1, unknown2);
    }
}
