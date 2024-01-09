package be.twofold.valen.converter.decoder;

import be.twofold.valen.core.math.*;

public final class BC1Decoder extends BCDecoder {
    private final boolean opaque;

    public BC1Decoder() {
        this(false);
    }

    BC1Decoder(boolean opaque) {
        super(8, 4);
        this.opaque = opaque;
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        int c0 = read16(src, srcPos);
        int c1 = read16(src, srcPos + 2);
        int bits = read32(src, srcPos + 4);

        float r0f = unpackR(c0), g0f = unpackG(c0), b0f = unpackB(c0);
        float r1f = unpackR(c1), g1f = unpackG(c1), b1f = unpackB(c1);

        byte r0 = MathF.packUNorm8(r0f), g0 = MathF.packUNorm8(g0f), b0 = MathF.packUNorm8(b0f);
        byte r1 = MathF.packUNorm8(r1f), g1 = MathF.packUNorm8(g1f), b1 = MathF.packUNorm8(b1f);
        byte r2, g2, b2;
        byte r3, g3, b3, a3;

        if (c0 > c1 || opaque) {
            r2 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 1.0f / 3.0f));
            g2 = MathF.packUNorm8(MathF.lerp(g0f, g1f, 1.0f / 3.0f));
            b2 = MathF.packUNorm8(MathF.lerp(b0f, b1f, 1.0f / 3.0f));
            r3 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 2.0f / 3.0f));
            g3 = MathF.packUNorm8(MathF.lerp(g0f, g1f, 2.0f / 3.0f));
            b3 = MathF.packUNorm8(MathF.lerp(b0f, b1f, 2.0f / 3.0f));
            a3 = (byte) 0xff;
        } else {
            r2 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 1.0f / 2.0f));
            g2 = MathF.packUNorm8(MathF.lerp(g0f, g1f, 1.0f / 2.0f));
            b2 = MathF.packUNorm8(MathF.lerp(b0f, b1f, 1.0f / 2.0f));
            r3 = (byte) 0x00;
            g3 = (byte) 0x00;
            b3 = (byte) 0x00;
            a3 = (byte) 0x00;
        }

        for (int y = 0, shift = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, shift += 2) {
                int colorIndex = (bits >> shift) & 3;
                switch (colorIndex) {
                    case 0 -> setPixel(dst, dstPos, r0, g0, b0, (byte) 0xff);
                    case 1 -> setPixel(dst, dstPos, r1, g1, b1, (byte) 0xff);
                    case 2 -> setPixel(dst, dstPos, r2, g2, b2, (byte) 0xff);
                    case 3 -> setPixel(dst, dstPos, r3, g3, b3, a3);
                }
                dstPos += bpp;
            }
            dstPos += bpr - 4 * bpp;
        }
    }

    private void setPixel(byte[] dst, int dstOff, byte r, byte g, byte b, byte a) {
        dst[dstOff] = r;
        dst[dstOff + 1] = g;
        dst[dstOff + 2] = b;
        dst[dstOff + 3] = a;
    }

    private static int read16(byte[] src, int srcPos) {
        int b0 = Byte.toUnsignedInt(src[srcPos]);
        int b1 = Byte.toUnsignedInt(src[srcPos + 1]);
        return b0 | b1 << 8;
    }

    private static int read32(byte[] src, int srcPos) {
        int b0 = Byte.toUnsignedInt(src[srcPos]);
        int b1 = Byte.toUnsignedInt(src[srcPos + 1]);
        int b2 = Byte.toUnsignedInt(src[srcPos + 2]);
        int b3 = Byte.toUnsignedInt(src[srcPos + 3]);
        return b0 | b1 << 8 | b2 << 16 | b3 << 24;
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
