package be.twofold.valen.converter.decoder;

public final class BC4UDecoder extends BCDecoder {
    private final int rOff;

    /**
     * Create a new BC4 decoder.
     *
     * @param bpp  The number of bytes per pixel.
     * @param rOff The offset of the red component.
     */
    public BC4UDecoder(int bpp, int rOff) {
        super(8, bpp);
        if (rOff < 0 || rOff >= bpp) {
            throw new IllegalArgumentException("rOff must be in range [0, bpp)");
        }

        this.rOff = rOff;
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        byte r0 = src[srcPos + 0];
        byte r1 = src[srcPos + 1];
        long indices = Byte.toUnsignedLong(src[srcPos + 7]) << 40 |
                       Byte.toUnsignedLong(src[srcPos + 6]) << 32 |
                       Byte.toUnsignedLong(src[srcPos + 5]) << 24 |
                       Byte.toUnsignedLong(src[srcPos + 4]) << 16 |
                       Byte.toUnsignedLong(src[srcPos + 3]) << 8 |
                       Byte.toUnsignedLong(src[srcPos + 2]);

        float r0f = Byte.toUnsignedInt(r0) / 255.0f;
        float r1f = Byte.toUnsignedInt(r1) / 255.0f;
        byte r2, r3, r4, r5, r6, r7;
        if (r0f > r1f) {
            r2 = pack(lerp(r0f, r1f, 1.0f / 7.0f));
            r3 = pack(lerp(r0f, r1f, 2.0f / 7.0f));
            r4 = pack(lerp(r0f, r1f, 3.0f / 7.0f));
            r5 = pack(lerp(r0f, r1f, 4.0f / 7.0f));
            r6 = pack(lerp(r0f, r1f, 5.0f / 7.0f));
            r7 = pack(lerp(r0f, r1f, 6.0f / 7.0f));
        } else {
            r2 = pack(lerp(r0f, r1f, 1.0f / 5.0f));
            r3 = pack(lerp(r0f, r1f, 2.0f / 5.0f));
            r4 = pack(lerp(r0f, r1f, 3.0f / 5.0f));
            r5 = pack(lerp(r0f, r1f, 4.0f / 5.0f));
            r6 = (byte) 0x00;
            r7 = (byte) 0xff;
        }

        dstPos += rOff;
        for (int y = 0, shift = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, shift += 3) {
                int colorIndex = (int) ((indices >> shift) & 7);
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

}
