package be.twofold.valen.reader.image;

import java.nio.*;

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
    static final int Size = 0x24;

    public static ImageMip read(ByteBuffer buffer) {
        int mipLevel = buffer.getInt(0x00);
        int mipSlice = buffer.getInt(0x04);
        int mipPixelWidth = buffer.getInt(0x08);
        int mipPixelHeight = buffer.getInt(0x0c);
        int decompressedSize = buffer.getInt(0x14);
        boolean flagIsCompressed = buffer.getInt(0x18) != 0;
        int compressedSize = buffer.getInt(0x1c);
        int cumulativeSizeStreamDB = buffer.getInt(0x20);

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
