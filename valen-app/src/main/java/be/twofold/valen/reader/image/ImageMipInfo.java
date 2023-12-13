package be.twofold.valen.reader.image;

import be.twofold.valen.core.util.*;

public record ImageMipInfo(
    int mipLevel,
    int mipSlice,
    int mipPixelWidth,
    int mipPixelHeight,
    int decompressedSize,
    boolean flagIsCompressed,
    int compressedSize,
    int cumulativeSizeStreamDB
) {
    public static ImageMipInfo read(BetterBuffer buffer) {
        var mipLevel = buffer.getInt();
        var mipSlice = buffer.getInt();
        var mipPixelWidth = buffer.getInt();
        var mipPixelHeight = buffer.getInt();
        buffer.expectInt(1);
        var decompressedSize = buffer.getInt();
        var flagIsCompressed = buffer.getIntAsBool();
        var compressedSize = buffer.getInt();
        var cumulativeSizeStreamDB = buffer.getInt();

        return new ImageMipInfo(
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
