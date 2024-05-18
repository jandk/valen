package be.twofold.valen.converter.decoder;

import be.twofold.valen.core.math.*;

public final class NormalDecoder extends BCDecoder {
    private static final byte[] Lut = initializeLut();

    public NormalDecoder(int bpb, int bpp) {
        super(bpb, bpp);
    }

    static byte lookup(byte r, byte g) {
        return Lut[Byte.toUnsignedInt(g) * 256 + Byte.toUnsignedInt(r)];
    }

    private static byte[] initializeLut() {
        var lut = new byte[256 * 256];
        for (var y = 0; y < 256; y++) {
            for (var x = 0; x < 256; x++) {
                var xx = MathF.unpackUNorm8Normal((byte) x);
                var yy = MathF.unpackUNorm8Normal((byte) y);
                var nz = MathF.sqrt(1.0f - MathF.clamp01(xx * xx + yy * yy));
                lut[y * 256 + x] = MathF.packUNorm8Normal(nz);
            }
        }
        return lut;
    }

    @Override
    public void decodeBlock(byte[] src, int srcPos, byte[] dst, int dstPos, int bpr) {
        for (int y = 0, shift = 0; y < 4; y++) {
            for (var x = 0; x < 4; x++, shift += 3) {
                var r = dst[dstPos];
                var g = dst[dstPos + 1];
                dst[dstPos + 2] = lookup(r, g);
                dstPos += bpp;
            }
            dstPos += bpr - 4 * bpp;
        }
    }
}
