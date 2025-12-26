package be.twofold.valen.game.eternal.reader.resource;

import be.twofold.valen.core.io.*;

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
        source.readInt();
        // source.expectInt(1); // depSubType
        var hashOrTimestamp = source.readLong();

        return new ResourcesDependency(
            type,
            name,
            depType,
            hashOrTimestamp
        );
    }
}
