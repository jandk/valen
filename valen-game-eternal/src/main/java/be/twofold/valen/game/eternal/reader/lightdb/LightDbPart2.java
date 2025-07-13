package be.twofold.valen.game.eternal.reader.lightdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record LightDbPart2(
    int id,
    float unk1,
    float unk2
) {
    public static LightDbPart2 read(BinaryReader reader) throws IOException {
        var id = reader.readInt();
        var unk1 = reader.readFloat();
        var unk2 = reader.readFloat();

        return new LightDbPart2(
            id,
            unk1,
            unk2
        );
    }
}
