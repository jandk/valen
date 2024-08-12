package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesGroup2(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesGroup2 read(DataSource source) throws IOException {
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        return new MapFileStaticInstancesGroup2(unknown1, unknown2);
    }
}
