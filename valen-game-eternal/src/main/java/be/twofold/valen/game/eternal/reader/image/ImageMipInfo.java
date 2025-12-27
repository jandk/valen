package be.twofold.valen.game.eternal.reader.image;

import wtf.reversed.toolbox.io.*;

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
    public static ImageMipInfo read(BinarySource source) throws IOException {
        var mipLevel = source.readInt();
        var mipSlice = source.readInt();
        var mipPixelWidth = source.readInt();
        var mipPixelHeight = source.readInt();
        source.expectInt(1);
        var decompressedSize = source.readInt();
        var flagIsCompressed = source.readBool(BoolFormat.INT);
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
