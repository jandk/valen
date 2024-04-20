package be.twofold.valen.reader.filecompressed;

import be.twofold.valen.core.util.*;

public record FileCompressedHeader(
    int uncompressedSize,
    int compressedSize
) {
    public static final int BYTES = 16;

    public static FileCompressedHeader read(BetterBuffer buffer) {
        int uncompressedSize = buffer.getLongAsInt();
        int compressedSize = buffer.getLongAsInt();
        return new FileCompressedHeader(uncompressedSize, compressedSize);
    }
}
