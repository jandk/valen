package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LightDbImageHeader(
    long hash,
    int unk1,
    int unk2,
    int unk3,
    int unk4,
    int unk5,
    int unk6
) {
    public static LightDbImageHeader read(BinaryReader reader) throws IOException {
        var hash = reader.readLong();
        var unk1 = reader.readInt();
        var unk2 = reader.readInt();
        var unk3 = reader.readInt();
        var unk4 = reader.readInt();
        var unk5 = reader.readInt();
        var unk6 = reader.readInt();

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
