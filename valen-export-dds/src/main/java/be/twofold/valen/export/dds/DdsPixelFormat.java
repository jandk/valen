package be.twofold.valen.export.dds;

import wtf.reversed.toolbox.io.*;

import java.io.*;

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

    public void writeTo(BinaryWriter writer) throws IOException {
        writer.writeInt(SIZE);
        writer.writeInt(flags);
        writer.writeInt(fourCC);
        writer.writeInt(rgbBitCount);
        writer.writeInt(rBitMask);
        writer.writeInt(gBitMask);
        writer.writeInt(bBitMask);
        writer.writeInt(aBitMask);
    }
}
