package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LightDbPart1(
    int offset,
    int length,
    int unk3
) {
    public static LightDbPart1 read(BinaryReader reader) throws IOException {
        var offset = reader.readInt();
        reader.expectInt(0);
        var length = reader.readInt();
        var unk3 = reader.readInt();

        return new LightDbPart1(
            offset,
            length,
            unk3
        );
    }
}
