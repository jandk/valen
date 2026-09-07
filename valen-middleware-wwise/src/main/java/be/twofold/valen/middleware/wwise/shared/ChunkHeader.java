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
        Check.positiveOrZero(size, "size");
    }

    public static ChunkHeader read(BinarySource source) throws IOException {
        var tag = ChunkTag.fromValue(source.readInt());
        var size = source.readInt();

        return new ChunkHeader(
            tag,
            size
        );
    }
}
