package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record MapFileStaticInstancesMaterialGroup(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesMaterialGroup read(BinaryReader reader) throws IOException {
        var unknown1 = reader.readInt();
        var unknown2 = reader.readInt();
        return new MapFileStaticInstancesMaterialGroup(unknown1, unknown2);
    }
}
