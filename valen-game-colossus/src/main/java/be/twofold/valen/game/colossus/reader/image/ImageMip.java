package be.twofold.valen.game.colossus.reader.image;

import be.twofold.valen.core.io.*;

import java.io.*;

public record ImageMip(
    int mipLevel,
    int mipSlice,
    int mipPixelWidth,
    int mipPixelHeight,
    int decompressedSize,
    int compressionMode,
    int compressedSize,
    int cumulativeSizeStreamDB
) {
    public static ImageMip read(DataSource source) throws IOException {
        var mipLevel = source.readInt();
        var mipSlice = source.readInt();
        var mipPixelWidth = source.readInt();
        var mipPixelHeight = source.readInt();
        var decompressedSize = source.readInt();
        var compressionMode = source.readInt();
        var compressedSize = source.readInt();
        var cumulativeSizeStreamDB = source.readInt();

        return new ImageMip(
            mipLevel,
            mipSlice,
            mipPixelWidth,
            mipPixelHeight,
            decompressedSize,
            compressionMode,
            compressedSize,
            cumulativeSizeStreamDB
        );
    }
}
