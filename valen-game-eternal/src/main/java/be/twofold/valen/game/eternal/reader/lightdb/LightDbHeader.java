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
    public static LightDbHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x4c444202);
        reader.expectInt(2);
        reader.expectInt(2);
        reader.expectInt(1);
        reader.expectInt(28);
        reader.expectInt(2);
        var nameOffset = reader.readInt();
        var hashLength = reader.readInt();
        var imageCount = reader.readInt();
        reader.expectInt(21);
        var imageOffset = reader.readInt();
        var indexOffset = reader.readInt();
        var hashOffset = reader.readInt();
        var dbOffset16 = reader.readInt();

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
