package be.twofold.valen.core.texture;

public final class ReconstructZ {
    private static final byte[] NORMAL = new byte[256 * 256];

    static {
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                float xx = Math.fma(x, (1.0f / 127.5f), -1.0f);
                float yy = Math.fma(y, (1.0f / 127.5f), -1.0f);
                float sq = xx * xx + yy * yy;
                float nz = (float) Math.sqrt(1.0f - Math.min(1.0f, Math.max(sq, 0.0f)));
                NORMAL[y * 256 + x] = (byte) Math.fma(nz, 127.5f, 128.0f);
            }
        }
    }

    public void reconstructZ(Surface surface) {
//        for (int y = 0; y < BLOCK_HEIGHT; y++) {
//            for (int x = 0; x < BLOCK_WIDTH; x++) {
//                int r = Byte.toUnsignedInt(dst[dstPos + redOffset]);
//                int g = Byte.toUnsignedInt(dst[dstPos + greenOffset]);
//                dst[dstPos + blueOffset] = NORMAL[g * 256 + r];
//                dstPos += bytesPerPixel;
//            }
//            dstPos += bytesPerLine - BLOCK_WIDTH * bytesPerPixel;
//        }
    }
}
