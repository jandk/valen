package be.twofold.valen.game.gbfr.reader.index;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record FileEntry(
    int chunk,
    int length,
    int offset
) {
    public static FileEntry read(BinarySource source) throws IOException {
        var chunk = source.readInt();
        var length = source.readInt();
        var offset = source.readInt();

        return new FileEntry(
            chunk,
            length,
            offset
        );
    }
}
