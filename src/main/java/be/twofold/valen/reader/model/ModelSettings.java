package be.twofold.valen.reader.model;

import be.twofold.valen.*;

public record ModelSettings(
    float unkFloat,
    int unknown1,
    int unknown2
) {
    public static ModelSettings read(BetterBuffer buffer) {
        float unkFloat = buffer.getFloat();
        int unknown1 = buffer.getInt();
        int unknown2 = buffer.getInt();
        buffer.expectInt(0);
        return new ModelSettings(unkFloat, unknown1, unknown2);
    }
}
