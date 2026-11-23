package be.twofold.valen.middleware.wwise.shared;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public record ChunkHeader(
    ChunkTag tag,
    int size
) {
    public static final int BYTES = 8;

    public ChunkHeader {
        Check.nonNull(tag, "tag");
        Check.nonNegative(size, "size");
    }

    public static ChunkHeader read(BinaryReader reader) throws IOException {
        var tag = ChunkTag.fromValue(reader.readInt());
        var size = reader.readInt();

        return new ChunkHeader(
            tag,
            size
        );
    }
}
