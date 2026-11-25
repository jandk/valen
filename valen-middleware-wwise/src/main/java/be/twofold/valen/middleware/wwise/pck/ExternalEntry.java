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

    public static ExternalEntry read(BinaryReader reader) throws IOException {
        var fileId = reader.readLong();
        var blockSize = reader.readInt();
        var fileSize = reader.readInt();
        var startBlock = reader.readInt();
        var languageId = reader.readInt();

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
