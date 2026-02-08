package be.twofold.valen.game.greatcircle.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesDependency(
    long type,
    long name,
    int depType,
    int depSubType,
    long hashOrTimestamp
) {
    public static ResourcesDependency read(BinarySource source) throws IOException {
        var type = source.readLong();
        var name = source.readLong();
        var depType = source.readInt();
        var depSubType = source.readInt();
        var hashOrTimestamp = source.readLong();

        return new ResourcesDependency(
            type,
            name,
            depType,
            depSubType,
            hashOrTimestamp
        );
    }
}
