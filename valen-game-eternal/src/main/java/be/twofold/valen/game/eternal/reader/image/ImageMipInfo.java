package be.twofold.valen.game.eternal.reader.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ImageMipInfo(
    int mipLevel,
    int mipSlice,
    int mipPixelWidth,
    int mipPixelHeight,
    int decompressedSize,
    int flagIsCompressed,
    int compressedSize,
    int cumulativeSizeStreamDb
) {
    public static ImageMipInfo read(BinarySource source) throws IOException {
        var mipLevel = source.readInt();
        var mipSlice = source.readInt();
        var mipPixelWidth = source.readInt();
        var mipPixelHeight = source.readInt();
        source.expectInt(0x1); // always1
        var decompressedSize = source.readInt();
        var flagIsCompressed = source.readInt();
        var compressedSize = source.readInt();
        var cumulativeSizeStreamDb = source.readInt();

        return new ImageMipInfo(
            mipLevel,
            mipSlice,
            mipPixelWidth,
            mipPixelHeight,
            decompressedSize,
            flagIsCompressed,
            compressedSize,
            cumulativeSizeStreamDb
        );
    }
}
