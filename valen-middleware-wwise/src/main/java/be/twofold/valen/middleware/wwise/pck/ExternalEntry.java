package be.twofold.valen.middleware.wwise.pck;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ExternalEntry(
    long fileId,
    int blockSize,
    int fileSize,
    int startBlock,
    int languageId
) {
    static final int BYTES = 24;

    public static ExternalEntry read(BinarySource source) throws IOException {
        var fileId = source.readLong();
        var blockSize = source.readInt();
        var fileSize = source.readInt();
        var startBlock = source.readInt();
        var languageId = source.readInt();

        return new ExternalEntry(
            fileId,
            blockSize,
            fileSize,
            startBlock,
            languageId
        );
    }

    public long offset() {
        return (long) startBlock * (long) blockSize;
    }
}
