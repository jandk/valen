package be.twofold.valen.reader.compfile;

import be.twofold.valen.core.util.*;

public record CompFileHeader(
    int uncompressedSize,
    int compressedSize
) {
    public static final int BYTES = 16;

    public static CompFileHeader read(BetterBuffer buffer) {
        int uncompressedSize = buffer.getLongAsInt();
        int compressedSize = buffer.getLongAsInt();
        return new CompFileHeader(uncompressedSize, compressedSize);
    }
}
