package be.twofold.valen.reader.resource;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ResourcesDependency(
    int type,
    int name,
    int depType,
    long hashOrTimestamp
) {
    public static final int BYTES = 32;

    public static ResourcesDependency read(DataSource source) throws IOException {
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
