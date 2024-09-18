package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LightDbHeader(
    int nameOffset,
    int hashLength,
    int imageCount,
    int imageOffset,
    int indexOffset,
    int hashOffset,
    int dbOffset16
) {
    public static LightDbHeader read(DataSource source) throws IOException {
        source.expectInt(0x4c444202);
        source.expectInt(2);
        source.expectInt(2);
        source.expectInt(1);
        source.expectInt(28);
        source.expectInt(2);
        var nameOffset = source.readInt();
        var hashLength = source.readInt();
        var imageCount = source.readInt();
        source.expectInt(21);
        var imageOffset = source.readInt();
        var indexOffset = source.readInt();
        var hashOffset = source.readInt();
        var dbOffset16 = source.readInt();

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
