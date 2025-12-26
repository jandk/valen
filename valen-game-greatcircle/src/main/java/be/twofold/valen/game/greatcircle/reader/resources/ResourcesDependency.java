package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesDependency(
    int type,
    int name,
    int depType,
    long hashOrTimestamp
) {
    public static ResourcesDependency read(BinarySource source) throws IOException {
        var type = source.readLongAsInt();
        var name = source.readLongAsInt();
        var depType = source.readInt();
        source.expectInt(1); // depSubType
        var hashOrTimestamp = source.readLong();

        return new ResourcesDependency(
            type,
            name,
            depType,
            hashOrTimestamp
        );
    }
}
