package be.twofold.valen.game.eternal.reader.lightdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record LightDbPart2(
    int id,
    float unk1,
    float unk2
) {
    public static LightDbPart2 read(BinarySource source) throws IOException {
        var id = source.readInt();
        var unk1 = source.readFloat();
        var unk2 = source.readFloat();

        return new LightDbPart2(
            id,
            unk1,
            unk2
        );
    }
}
