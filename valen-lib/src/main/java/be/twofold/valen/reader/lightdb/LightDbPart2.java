package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.util.*;

public record LightDbPart2(
    int id,
    float unk1,
    float unk2
) {
    public static final int BYTES = 12;

    public static LightDbPart2 read(BetterBuffer buffer) {
        var id = buffer.getInt();
        var unk1 = buffer.getFloat();
        var unk2 = buffer.getFloat();

        return new LightDbPart2(
            id,
            unk1,
            unk2
        );
    }
}
