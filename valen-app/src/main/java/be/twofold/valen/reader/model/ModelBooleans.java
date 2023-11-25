package be.twofold.valen.reader.model;

import be.twofold.valen.core.util.*;

public record ModelBooleans(
    byte unkBool1,
    byte unkBool2
) {
    public static ModelBooleans read(BetterBuffer buffer) {
        byte unkBool1 = buffer.getByte();
        byte unkBool2 = buffer.getByte();
        buffer.skip(2);
        return new ModelBooleans(unkBool1, unkBool2);
    }
}
