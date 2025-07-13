package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesGroup3(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesGroup3 read(BinaryReader reader) throws IOException {
        var unknown1 = reader.readInt();
        var unknown2 = reader.readInt();
        return new MapFileStaticInstancesGroup3(unknown1, unknown2);
    }
}
