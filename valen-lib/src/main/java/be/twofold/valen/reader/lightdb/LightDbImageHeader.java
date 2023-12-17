package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.util.*;

public record LightDbImageHeader(
    long hash,
    int unk1,
    int unk2,
    int unk3,
    int unk4,
    int unk5,
    int unk6
) {
    public static final int BYTES = 32;

    public static LightDbImageHeader read(BetterBuffer buffer) {
        var hash = buffer.getLong();
        var unk1 = buffer.getInt();
        var unk2 = buffer.getInt();
        var unk3 = buffer.getInt();
        var unk4 = buffer.getInt();
        var unk5 = buffer.getInt();
        var unk6 = buffer.getInt();

        return new LightDbImageHeader(
            hash,
            unk1,
            unk2,
            unk3,
            unk4,
            unk5,
            unk6
        );
    }
}
