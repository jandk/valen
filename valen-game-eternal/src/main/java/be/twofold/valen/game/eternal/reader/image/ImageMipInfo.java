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
    public static ImageMipInfo read(BinaryReader reader) throws IOException {
        var mipLevel = reader.readInt();
        var mipSlice = reader.readInt();
        var mipPixelWidth = reader.readInt();
        var mipPixelHeight = reader.readInt();
        reader.expectInt(0x1); // always1
        var decompressedSize = reader.readInt();
        var flagIsCompressed = reader.readInt();
        var compressedSize = reader.readInt();
        var cumulativeSizeStreamDb = reader.readInt();

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
