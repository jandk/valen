package be.twofold.valen.game.dyinglight.reader.mesh;

import be.twofold.valen.core.io.*;

import java.io.*;

public record DLMaterial(
    int unk1,
    int unk2,
    String name,
    int unk4,
    int unk5,
    int skinned,
    int unk6
) {
    public static DLMaterial read(BinaryReader reader) throws IOException {
        var unk1 = reader.readInt();
        var unk2 = reader.readInt();
        var name = OffsetString.read(reader, true);
        var unk4 = reader.readInt();
        var unk5 = reader.readInt();
        var skinned = reader.readInt();
        var unk6 = reader.readInt();
        return new DLMaterial(unk1, unk2, name, unk4, unk5, skinned, unk6);
    }

}
