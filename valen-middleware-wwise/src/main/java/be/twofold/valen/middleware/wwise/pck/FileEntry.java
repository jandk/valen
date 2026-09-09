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

    public static <T> FileEntry<T> read(BinarySource source, IntFunction<T> idMapper) throws IOException {
        var fileId = idMapper.apply(source.readInt());
        var blockSize = source.readInt();
        var fileSize = source.readInt();
        var startBlock = source.readInt();
        var languageId = source.readInt();

        return new FileEntry<>(fileId, blockSize, fileSize, startBlock, languageId);
    }

    public long offset() {
        return (long) startBlock * (long) blockSize;
    }
}
