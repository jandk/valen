package be.twofold.valen.middleware.wwise.pck;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record FileEntry(
    int fileId,
    int blockSize,
    int fileSize,
    int startBlock,
    int languageId
) {
    static final int BYTES = 20;

    public static FileEntry read(BinarySource source) throws IOException {
        var fileId = source.readInt();
        var blockSize = source.readInt();
        var fileSize = source.readInt();
        var startBlock = source.readInt();
        var languageId = source.readInt();

        return new FileEntry(fileId, blockSize, fileSize, startBlock, languageId);
    }

    public long offset() {
        return (long) startBlock * (long) blockSize;
    }
}
