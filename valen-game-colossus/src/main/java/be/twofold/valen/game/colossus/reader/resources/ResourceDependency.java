package be.twofold.valen.game.colossus.reader.resources;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourceDependency(
    long type,
    long name,
    int depType,
    int depSubType,
    long hashOrTimestamp
) {
    public static ResourceDependency read(DataSource source) throws IOException {
        var type = source.readLong();
        var name = source.readLong();
        var depType = source.readInt();
        var depSubType = source.readInt();
        var hashOrTimestamp = source.readLong();

        return new ResourceDependency(
            type,
            name,
            depType,
            depSubType,
            hashOrTimestamp
        );
    }
}
