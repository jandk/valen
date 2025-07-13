package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesGroup2(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesGroup2 read(BinaryReader reader) throws IOException {
        var unknown1 = reader.readInt();
        var unknown2 = reader.readInt();
        return new MapFileStaticInstancesGroup2(unknown1, unknown2);
    }
}
