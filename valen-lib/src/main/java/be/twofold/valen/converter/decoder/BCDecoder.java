package be.twofold.valen.converter.decoder;

import be.twofold.valen.core.util.*;

public abstract class BCDecoder {
    private final int bpb;
    final int bpp;

    BCDecoder(int bpb, int bpp) {
        Check.argument(bpp >= 1 && bpp <= 4, "bpp must be between 1 and 4");
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
        Check.argument(width % 4 == 0, "width must be a multiple of 4 for now");
        Check.argument(height % 4 == 0, "height must be a multiple of 4 for now");

        int expectedLength = ((width + 3) / 4) * ((height + 3) / 4) * bpb;
        Check.argument(src.length == expectedLength, () -> String.format("src has wrong length: expected of %d, got %d", expectedLength, src.length));

        byte[] dst = new byte[width * height * bpp];
        for (int y = 0, srcPos = 0; y < height; y += 4) {
            for (int x = 0; x < width; x += 4, srcPos += bpb) {
                decodeBlock(src, srcPos, dst, (y * width + x) * bpp, width * bpp);
            }
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

    static byte pack(float f) {
        return (byte) Math.fma(f, 255.0f, 0.5f);
    }

}
