package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.util.*;

public record LightDbPart1(
    int offset,
    int length,
    int unk3
) {
    public static final int BYTES = 16;

    public static LightDbPart1 read(BetterBuffer buffer) {
        var offset = buffer.getInt();
        buffer.expectInt(0);
        var length = buffer.getInt();
        var unk3 = buffer.getInt();

        return new LightDbPart1(
            offset,
            length,
            unk3
        );
    }
}
