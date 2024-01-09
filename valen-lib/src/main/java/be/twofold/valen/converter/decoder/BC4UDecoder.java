package be.twofold.valen.converter.decoder;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public final class BC4UDecoder extends BCDecoder {
    private final int rOff;

    /**
     * Create a new BC4 decoder.
     */
    public BC4UDecoder() {
        this(1, 0);
    }

    BC4UDecoder(int bpp, int rOff) {
        super(8, bpp);
        Check.argument(rOff >= 0 && rOff < bpp, "rOff must be in range [0, bpp)");

        this.rOff = rOff;
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        byte r0 = src[srcPos];
        byte r1 = src[srcPos + 1];
        long bits = read48(src, srcPos + 2);

        float r0f = MathF.unpackUNorm8(r0);
        float r1f = MathF.unpackUNorm8(r1);
        byte r2, r3, r4, r5, r6, r7;
        if (r0f > r1f) {
            r2 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 1.0f / 7.0f));
            r3 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 2.0f / 7.0f));
            r4 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 3.0f / 7.0f));
            r5 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 4.0f / 7.0f));
            r6 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 5.0f / 7.0f));
            r7 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 6.0f / 7.0f));
        } else {
            r2 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 1.0f / 5.0f));
            r3 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 2.0f / 5.0f));
            r4 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 3.0f / 5.0f));
            r5 = MathF.packUNorm8(MathF.lerp(r0f, r1f, 4.0f / 5.0f));
            r6 = (byte) 0x00;
            r7 = (byte) 0xff;
        }

        dstPos += rOff;
        for (int y = 0, shift = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, shift += 3) {
                int colorIndex = (int) ((bits >> shift) & 7);
                switch (colorIndex) {
                    case 0 -> dst[dstPos] = r0;
                    case 1 -> dst[dstPos] = r1;
                    case 2 -> dst[dstPos] = r2;
                    case 3 -> dst[dstPos] = r3;
                    case 4 -> dst[dstPos] = r4;
                    case 5 -> dst[dstPos] = r5;
                    case 6 -> dst[dstPos] = r6;
                    case 7 -> dst[dstPos] = r7;
                }
                dstPos += bpp;
            }
            dstPos += bpr - 4 * bpp;
        }
    }

    private static long read48(byte[] src, int srcPos) {
        long b0 = Byte.toUnsignedLong(src[srcPos]);
        long b1 = Byte.toUnsignedLong(src[srcPos + 1]);
        long b2 = Byte.toUnsignedLong(src[srcPos + 2]);
        long b3 = Byte.toUnsignedLong(src[srcPos + 3]);
        long b4 = Byte.toUnsignedLong(src[srcPos + 4]);
        long b5 = Byte.toUnsignedLong(src[srcPos + 5]);

        return b0 | b1 << 8 | b2 << 16 | b3 << 24 | b4 << 32 | b5 << 40;
    }
}
