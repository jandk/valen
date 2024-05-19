package be.twofold.valen.core.texture.decoder;

import be.twofold.valen.core.util.*;

public abstract class BCDecoder {
    private final int bpb;
    final int bpp;

    BCDecoder(int bpb, int bpp) {
        Check.argument(bpp >= 1 && bpp <= 6, "bpp must be between 1 and 6");
        this.bpb = bpb;
        this.bpp = bpp;
    }

    /**
     * Decodes a Block Compressed image.
     *
     * @param src    The source data. Must be a multiple of 8 bytes.
     * @param width  The width of the image. Must be a multiple of 4 for now.
     * @param height The height of the image. Must be a multiple of 4 for now.
     */
    public byte[] decode(byte[] src, int width, int height) {
        Check.notNull(src, "src is null");
        Check.argument(width > 0, "width must be greater than 0");
        Check.argument(height > 0, "height must be greater than 0");

        int blockWidth = (width + 3) / 4;
        int blockHeight = (height + 3) / 4;
        int expectedLength = blockWidth * blockHeight * bpb;
        Check.argument(src.length == expectedLength, () -> String.format("src has wrong length: expected %d, got %d", expectedLength, src.length));

        int realWidth = blockWidth * 4;
        int realHeight = blockHeight * 4;
        int stride = realWidth * bpp;
        byte[] dst = new byte[realWidth * realHeight * bpp];
        for (int y = 0, srcPos = 0; y < height; y += 4) {
            for (int x = 0; x < width; x += 4, srcPos += bpb) {
                decodeBlock(src, srcPos, dst, (y * realWidth + x) * bpp, stride);
            }
        }

        if (realWidth != width || realHeight != height) {
            byte[] result = new byte[width * height * bpp];

            int srcPos = 0;
            int dstPos = 0;
            for (int y = 0; y < height; y++) {
                System.arraycopy(dst, srcPos, result, dstPos, width * bpp);
                srcPos += realWidth * bpp;
                dstPos += width * bpp;
            }
            return result;
        }

        return dst;
    }

    /**
     * Decodes a single block.
     *
     * @param src    The source data. Must be 8 bytes long.
     * @param srcPos The position in the source data.
     * @param dst    The destination data. Must be at least 16 * bpr bytes long.
     * @param dstPos The position in the destination data.
     * @param bpr    The bytes per row in the destination data.
     */
    public abstract void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr);
}
