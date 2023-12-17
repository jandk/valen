package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

public record ModelMisc1(
    float unkFloat,
    int unknown1,
    int unknown2
) {
    public static ModelMisc1 read(BetterBuffer buffer) {
        var unkFloat = buffer.getFloat();
        var unknown1 = buffer.getInt();
        var unknown2 = buffer.getInt();
        buffer.expectInt(0);
        return new ModelMisc1(unkFloat, unknown1, unknown2);
    }
}
