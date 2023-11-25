package be.twofold.valen.reader.image;

import be.twofold.valen.core.util.*;

public record ImageMip(
    int mipLevel,
    int mipSlice,
    int mipPixelWidth,
    int mipPixelHeight,
    int decompressedSize,
    boolean flagIsCompressed,
    int compressedSize,
    int cumulativeSizeStreamDB
) {
    public static ImageMip read(BetterBuffer buffer) {
        int mipLevel = buffer.getInt();
        int mipSlice = buffer.getInt();
        int mipPixelWidth = buffer.getInt();
        int mipPixelHeight = buffer.getInt();
        buffer.expectInt(1);
        int decompressedSize = buffer.getInt();
        boolean flagIsCompressed = buffer.getIntAsBool();
        int compressedSize = buffer.getInt();
        int cumulativeSizeStreamDB = buffer.getInt();

        return new ImageMip(
            mipLevel,
            mipSlice,
            mipPixelWidth,
            mipPixelHeight,
            decompressedSize,
            flagIsCompressed,
            compressedSize,
            cumulativeSizeStreamDB
        );
    }
}
