package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

public record ModelMisc2(
    byte unknown,
    byte unknownBool
) {
    public static ModelMisc2 read(BetterBuffer buffer) {
        byte unknown = buffer.getByte();
        byte unknownBool = buffer.getByte();
        buffer.expectShort(0);
        return new ModelMisc2(unknown, unknownBool);
    }
}
