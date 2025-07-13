package be.twofold.valen.game.darkages.reader.resources;

import be.twofold.valen.core.io.BinaryReader;

import java.io.*;

public record ResourcesDependency(
    int type,
    int name,
    int depType,
    long hashOrTimestamp
) {
    public static ResourcesDependency read(BinaryReader reader) throws IOException {
        var type = reader.readLongAsInt();
        var name = reader.readLongAsInt();
        var depType = reader.readInt();
        reader.readInt();
        // source.expectInt(1); // depSubType
        var hashOrTimestamp = reader.readLong();

        return new ResourcesDependency(
            type,
            name,
            depType,
            hashOrTimestamp
        );
    }
}
