package be.twofold.valen.game.gbfr.reader.index;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ChunkEntry(
    long offset,
    int compressedSize,
    int size,
    int alignment,
    boolean unknown,
    short fileId
) {
    public static ChunkEntry read(BinarySource source) throws IOException {
        var offset = source.readLong();
        var compressedSize = source.readInt();
        var size = source.readInt();
        var alignment = source.readInt();
        var unknown = source.readBool(BoolFormat.SHORT);
        var fileId = source.readShort();

        return new ChunkEntry(
            offset,
            compressedSize,
            size,
            alignment,
            unknown,
            fileId
        );
    }
}
