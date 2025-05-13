package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;

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
    public static ImageMipInfo read(DataSource source) throws IOException {
        var mipLevel = source.readInt();
        var mipSlice = source.readInt();
        var mipPixelWidth = source.readInt();
        var mipPixelHeight = source.readInt();
        source.expectInt(1);
        var decompressedSize = source.readInt();
        var flagIsCompressed = source.readBoolInt();
        var compressedSize = source.readInt();
        var cumulativeSizeStreamDB = source.readInt();

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
