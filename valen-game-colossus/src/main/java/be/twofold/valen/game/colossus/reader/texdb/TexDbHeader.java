package be.twofold.valen.game.colossus.reader.texdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record TexDbHeader(
    long magic,
    long hash1,
    long hash2,
    int numEntries
) {
    public static TexDbHeader read(DataSource source) throws IOException {
        var magic = source.readLong();
        var hash1 = source.readLong();
        var hash2 = source.readLong();
        var numEntries = source.readLongAsInt();

        return new TexDbHeader(
            magic,
            hash1,
            hash2,
            numEntries
        );
    }
}
