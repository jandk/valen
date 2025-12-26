package be.twofold.valen.game.eternal.reader.filecompressed;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record FileCompressedHeader(
    int uncompressedSize,
    int compressedSize
) {
    public static FileCompressedHeader read(BinarySource source) throws IOException {
        int uncompressedSize = source.readLongAsInt();
        int compressedSize = source.readLongAsInt();
        return new FileCompressedHeader(uncompressedSize, compressedSize);
    }
}
