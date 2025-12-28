package be.twofold.valen.game.colossus.reader.texdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record TexDbEntry(
    long hash,
    long offset
) {
    public static TexDbEntry read(BinarySource source) throws IOException {
        var hash = source.readLong();
        var offset = source.readLong();

        return new TexDbEntry(
            hash,
            offset
        );
    }
}
