package be.twofold.valen.reader.lightdb;

import be.twofold.valen.core.util.*;

public record LightDbHeader(
    int nameOffset,
    int hashLength,
    int imageCount,
    int imageOffset,
    int indexOffset,
    int hashOffset,
    int dbOffset16
) {
    public static final int BYTES = 56;

    public static LightDbHeader read(BetterBuffer buffer) {
        buffer.expectInt(0x4c444202);
        buffer.expectInt(2);
        buffer.expectInt(2);
        buffer.expectInt(1);
        buffer.expectInt(28);
        buffer.expectInt(2);
        var nameOffset = buffer.getInt();
        var hashLength = buffer.getInt();
        var imageCount = buffer.getInt();
        buffer.expectInt(21);
        var imageOffset = buffer.getInt();
        var indexOffset = buffer.getInt();
        var hashOffset = buffer.getInt();
        var dbOffset16 = buffer.getInt();

        return new LightDbHeader(
            nameOffset,
            hashLength,
            imageCount,
            imageOffset,
            indexOffset,
            hashOffset,
            dbOffset16
        );
    }
}
