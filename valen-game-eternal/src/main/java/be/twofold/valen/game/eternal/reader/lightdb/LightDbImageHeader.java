package be.twofold.valen.game.eternal.reader.lightdb;

import wtf.reversed.toolbox.io.*;

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
    public static LightDbImageHeader read(BinarySource source) throws IOException {
        var hash = source.readLong();
        var unk1 = source.readInt();
        var unk2 = source.readInt();
        var unk3 = source.readInt();
        var unk4 = source.readInt();
        var unk5 = source.readInt();
        var unk6 = source.readInt();

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
