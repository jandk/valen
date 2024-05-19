package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;

import java.io.*;

public record MapFileStaticInstancesMaterialGroup(
    int unknown1,
    int unknown2
) {
    public static MapFileStaticInstancesMaterialGroup read(DataSource source) throws IOException {
        var unknown1 = source.readInt();
        var unknown2 = source.readInt();
        return new MapFileStaticInstancesMaterialGroup(unknown1, unknown2);
    }
}
