package be.twofold.valen.middleware.wwise.pck;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.function.*;

public record FileEntry<T>(
    T fileId,
    int blockSize,
    int fileSize,
    int startBlock,
    int languageId
) {
    static final int BYTES = 20;

    public static <T> FileEntry<T> read(BinaryReader reader, IntFunction<T> idMapper) throws IOException {
        var fileId = idMapper.apply(reader.readInt());
        var blockSize = reader.readInt();
        var fileSize = reader.readInt();
        var startBlock = reader.readInt();
        var languageId = reader.readInt();

        return new FileEntry<>(fileId, blockSize, fileSize, startBlock, languageId);
    }

    public long offset() {
        return (long) startBlock * (long) blockSize;
    }
}
