package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.util.*;

public record LightDbIndexEntry(
    short imageIndex,
    short x,
    short y,
    short width,
    short height
) {
    public static final int BYTES = 10;

    public static LightDbIndexEntry read(BetterBuffer buffer) {
        var imageIndex = buffer.getShort();
        var x = buffer.getShort();
        var y = buffer.getShort();
        var width = buffer.getShort();
        var height = buffer.getShort();

        return new LightDbIndexEntry(
            imageIndex,
            x,
            y,
            width,
            height
        );
    }
}
