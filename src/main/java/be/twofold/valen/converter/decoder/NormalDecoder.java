package be.twofold.valen.converter.decoder;

public final class NormalDecoder extends BCDecoder {
    private static final byte[] Lut = initializeLut();

    private final int rOff;
    private final int gOff;
    private final int bOff;

    public NormalDecoder(int bpb, int bpp, int rOff, int gOff, int bOff) {
        super(bpb, bpp);
        this.rOff = rOff;
        this.gOff = gOff;
        this.bOff = bOff;
    }

    static byte lookup(byte r, byte g) {
        return Lut[Byte.toUnsignedInt(g) * 256 + Byte.toUnsignedInt(r)];
    }

    private static byte[] initializeLut() {
        byte[] lut = new byte[256 * 256];
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                lut[y * 256 + x] = computeZ(x, y);
            }
        }
        return lut;
    }

    private static byte computeZ(int x, int y) {
        float xx = Byte.toUnsignedInt((byte) x) / 255.0f;
        float yy = Byte.toUnsignedInt((byte) y) / 255.0f;
        float nx = (xx * 2.0f) - 1.0f;
        float ny = (yy * 2.0f) - 1.0f;
        float nz = (float) Math.sqrt(1.0f - Math.max(0.0f, Math.min(1.0f, nx * nx + ny * ny)));
        return (byte) Math.round(((nz + 1.0f) / 2.0f) * 255.0f);
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        for (int y = 0, shift = 0; y < 4; y++) {
            for (int x = 0; x < 4; x++, shift += 3) {
                byte r = dst[dstPos + rOff];
                byte g = dst[dstPos + gOff];
                byte b = lookup(r, g);
                dst[dstPos + bOff] = b;
                dstPos += bpp;
            }
            dstPos += bpr - 4 * bpp;
        }
    }
}
