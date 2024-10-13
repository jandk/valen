package be.twofold.valen.export.dds;

import be.twofold.valen.core.util.*;

import java.nio.*;

record DdsPixelFormat(
    int flags,
    int fourCC,
    int rgbBitCount,
    int rBitMask,
    int gBitMask,
    int bBitMask,
    int aBitMask
) {
    // dwFlags
    public static final int DDPF_ALPHAPIXELS = 0x1;
    public static final int DDPF_ALPHA = 0x2;
    public static final int DDPF_FOURCC = 0x4;
    public static final int DDPF_RGB = 0x40;
    public static final int DDPF_YUV = 0x200;
    public static final int DDPF_LUMINANCE = 0x20000;

    public static final int SIZE = 32;

    public ByteBuffer toBuffer() {
        return Buffers.allocate(SIZE)
            .putInt(SIZE)
            .putInt(flags)
            .putInt(fourCC)
            .putInt(rgbBitCount)
            .putInt(rBitMask)
            .putInt(gBitMask)
            .putInt(bBitMask)
            .putInt(aBitMask)
            .flip();
    }
}
