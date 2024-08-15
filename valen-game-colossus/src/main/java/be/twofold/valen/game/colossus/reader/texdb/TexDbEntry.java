package be.twofold.valen.game.colossus.reader.texdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record TexDbEntry(
    long hash,
    long offset
) {
    public static TexDbEntry read(DataSource source) throws IOException {
        var hash = source.readLong();
        var offset = source.readLong();

        return new TexDbEntry(
            hash,
            offset
        );
    }
}
