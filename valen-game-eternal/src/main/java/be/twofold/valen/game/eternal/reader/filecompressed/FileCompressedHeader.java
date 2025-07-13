package be.twofold.valen.game.eternal.reader.filecompressed;

import be.twofold.valen.core.io.*;

import java.io.*;

public record FileCompressedHeader(
    int uncompressedSize,
    int compressedSize
) {
    public static FileCompressedHeader read(BinaryReader reader) throws IOException {
        int uncompressedSize = reader.readLongAsInt();
        int compressedSize = reader.readLongAsInt();
        return new FileCompressedHeader(uncompressedSize, compressedSize);
    }
}
