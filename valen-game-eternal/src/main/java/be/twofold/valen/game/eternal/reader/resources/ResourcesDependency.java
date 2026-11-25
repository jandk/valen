package be.twofold.valen.game.eternal.reader.resources;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ResourcesDependency(
    long type,
    long name,
    int depType,
    int depSubType,
    long hashOrTimestamp
) {
    public static ResourcesDependency read(BinaryReader reader) throws IOException {
        var type = reader.readLong();
        var name = reader.readLong();
        var depType = reader.readInt();
        var depSubType = reader.readInt();
        var hashOrTimestamp = reader.readLong();

        return new ResourcesDependency(
            type,
            name,
            depType,
            depSubType,
            hashOrTimestamp
        );
    }
}
