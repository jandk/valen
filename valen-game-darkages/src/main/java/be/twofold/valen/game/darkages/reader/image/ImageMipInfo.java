package be.twofold.valen.game.darkages.reader.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record ImageMipInfo(
    int mipLevel,
    int mipSlice,
    int mipPixelWidth,
    int mipPixelHeight,
    int mipPixelDepth,
    int decompressedSize,
    boolean flagIsCompressed,
    int compressedSize,
    int cumulativeSizeStreamDB
) {
    public static ImageMipInfo read(BinaryReader reader) throws IOException {
        var mipLevel = reader.readInt();
        var mipSlice = reader.readInt();
        var mipPixelWidth = reader.readInt();
        var mipPixelHeight = reader.readInt();
        var mipPixelDepth = reader.readInt();
        var decompressedSize = reader.readInt();
        var flagIsCompressed = reader.readBool(BoolFormat.INT);
        var compressedSize = reader.readInt();
        var cumulativeSizeStreamDB = reader.readInt();

        return new ImageMipInfo(
            mipLevel,
            mipSlice,
            mipPixelWidth,
            mipPixelHeight,
            mipPixelDepth,
            decompressedSize,
            flagIsCompressed,
            compressedSize,
            cumulativeSizeStreamDB
        );
    }
}
