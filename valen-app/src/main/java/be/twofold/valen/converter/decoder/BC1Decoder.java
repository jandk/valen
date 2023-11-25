package be.twofold.valen.converter.decoder;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public final class BC1Decoder extends BCDecoder {
    private final int rOff;
    private final int gOff;
    private final int bOff;

    /**
     * Create a new BC1 decoder.
     *
     * @param bpp  The number of bytes per pixel.
     * @param rOff The offset of the red component.
     * @param gOff The offset of the green component.
     * @param bOff The offset of the blue component.
     */
    public BC1Decoder(int bpp, int rOff, int gOff, int bOff) {
        super(8, bpp);
        Check.argument(rOff >= 0 && rOff < bpp, "rOff must be in range [0, bpp)");
        Check.argument(gOff >= 0 && gOff < bpp, "gOff must be in range [0, bpp)");
        Check.argument(bOff >= 0 && bOff < bpp, "bOff must be in range [0, bpp)");

        this.rOff = rOff;
        this.gOff = gOff;
        this.bOff = bOff;
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        int c0 = src[srcPos + 0] & 0xff | (src[srcPos + 1] & 0xff) << 8;
        int c1 = src[srcPos + 2] & 0xff | (src[srcPos + 3] & 0xff) << 8;
        srcPos += 4;

        float r0f = unpackR(c0), g0f = unpackG(c0), b0f = unpackB(c0);
        float r1f = unpackR(c1), g1f = unpackG(c1), b1f = unpackB(c1);

        byte r0 = pack(r0f), g0 = pack(g0f), b0 = pack(b0f);
        byte r1 = pack(r1f), g1 = pack(g1f), b1 = pack(b1f);
        byte r2, g2, b2;
        byte r3, g3, b3;

        if (c0 > c1) {
            r2 = pack(MathF.lerp(r0f, r1f, 1f / 3f));
            g2 = pack(MathF.lerp(g0f, g1f, 1f / 3f));
            b2 = pack(MathF.lerp(b0f, b1f, 1f / 3f));
            r3 = pack(MathF.lerp(r0f, r1f, 2f / 3f));
            g3 = pack(MathF.lerp(g0f, g1f, 2f / 3f));
            b3 = pack(MathF.lerp(b0f, b1f, 2f / 3f));
        } else {
            r2 = pack(MathF.lerp(r0f, r1f, .5f));
            g2 = pack(MathF.lerp(g0f, g1f, .5f));
            b2 = pack(MathF.lerp(b0f, b1f, .5f));
            r3 = 0;
            g3 = 0;
            b3 = 0;
        }

        for (int y = 0; y < 4; y++) {
            byte index = src[srcPos++];
            for (int x = 0; x < 4; x++) {
                switch ((index >> (x << 1)) & 3) {
                    case 0 -> setPixel(dst, dstPos, r0, g0, b0);
                    case 1 -> setPixel(dst, dstPos, r1, g1, b1);
                    case 2 -> setPixel(dst, dstPos, r2, g2, b2);
                    case 3 -> setPixel(dst, dstPos, r3, g3, b3);
                }
                dstPos += bpp;
            }
            dstPos += bpr - 4 * bpp;
        }
    }

    private void setPixel(byte[] dst, int dstOff, byte r, byte g, byte b) {
        dst[dstOff + rOff] = r;
        dst[dstOff + gOff] = g;
        dst[dstOff + bOff] = b;
    }

    private static float unpackR(int c) {
        return ((c >> 11) & 31) * (1f / 31f);
    }

    private static float unpackG(int c) {
        return ((c >> 5) & 63) * (1f / 63f);
    }

    private static float unpackB(int c) {
        return ((c) & 31) * (1f / 31f);
    }

}
