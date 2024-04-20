package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.util.*;

public record StaticModelMisc1(
    float unkFloat,
    int unknown1,
    int unknown2
) {
    public static StaticModelMisc1 read(BetterBuffer buffer) {
        var unkFloat = buffer.getFloat();
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        buffer.expectInt(0);
        return new StaticModelMisc1(unkFloat, unknown1, unknown2);
    }
}
