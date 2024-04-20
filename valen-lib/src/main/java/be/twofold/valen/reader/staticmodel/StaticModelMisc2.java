package be.twofold.valen.reader.staticmodel;

import be.twofold.valen.core.util.*;

public record StaticModelMisc2(
    byte unknown,
    byte unknownBool
) {
    public static StaticModelMisc2 read(BetterBuffer buffer) {
        var unknown = buffer.getByte();
        var unknownBool = buffer.getByte();
        buffer.expectShort(0);
        return new StaticModelMisc2(unknown, unknownBool);
    }
}
