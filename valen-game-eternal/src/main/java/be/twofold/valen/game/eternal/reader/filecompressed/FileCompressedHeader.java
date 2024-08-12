package be.twofold.valen.game.eternal.reader.filecompressed;

import be.twofold.valen.core.io.*;

import java.io.*;

public record FileCompressedHeader(
    int uncompressedSize,
    int compressedSize
) {
    public static final int BYTES = 16;

    public static FileCompressedHeader read(DataSource source) throws IOException {
        int uncompressedSize = source.readLongAsInt();
        int compressedSize = source.readLongAsInt();
        return new FileCompressedHeader(uncompressedSize, compressedSize);
    }
}
