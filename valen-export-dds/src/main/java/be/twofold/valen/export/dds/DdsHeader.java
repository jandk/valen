package be.twofold.valen.export.dds;

import be.twofold.valen.core.util.*;

import java.nio.*;

record DdsHeader(
    int flags,
    int height,
    int width,
    int pitchOrLinearSize,
    int depth,
    int mipMapCount,
    DdsPixelFormat pixelFormat,
    int caps1,
    int caps2,
    DdsHeaderDxt10 header10
) {
    // dwFlags
    public static final int DDSD_CAPS = 0x1;
    public static final int DDSD_HEIGHT = 0x2;
    public static final int DDSD_WIDTH = 0x4;
    public static final int DDSD_PITCH = 0x8;
    public static final int DDSD_PIXELFORMAT = 0x1000;
    public static final int DDSD_MIPMAPCOUNT = 0x20000;
    public static final int DDSD_LINEARSIZE = 0x80000;
    public static final int DDSD_DEPTH = 0x800000;
    public static final int DDS_HEADER_FLAGS_TEXTURE = DDSD_CAPS | DDSD_HEIGHT | DDSD_WIDTH | DDSD_PIXELFORMAT;

    // dwCaps1
    public static final int DDSCAPS_COMPLEX = 0x8;
    public static final int DDSCAPS_TEXTURE = 0x1000;
    public static final int DDSCAPS_MIPMAP = 0x400000;

    // dwCaps2
    public static final int DDSCAPS2_CUBEMAP = 0x200;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEX = 0x400;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEX = 0x800;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEY = 0x1000;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEY = 0x2000;
    public static final int DDSCAPS2_CUBEMAP_POSITIVEZ = 0x4000;
    public static final int DDSCAPS2_CUBEMAP_NEGATIVEZ = 0x8000;
    public static final int DDSCAPS2_CUBEMAP_ALL_FACES =
        DDSCAPS2_CUBEMAP_POSITIVEX | DDSCAPS2_CUBEMAP_NEGATIVEX |
        DDSCAPS2_CUBEMAP_POSITIVEY | DDSCAPS2_CUBEMAP_NEGATIVEY |
        DDSCAPS2_CUBEMAP_POSITIVEZ | DDSCAPS2_CUBEMAP_NEGATIVEZ;
    public static final int DDSCAPS2_VOLUME = 0x200000;

    public static final int SIZE = 124;

    public ByteBuffer toBuffer() {
        var dxt10Size = header10 == null ? 0 : DdsHeaderDxt10.SIZE;
        var dxt10Buffer = header10 == null ? ByteBuffer.allocate(0) : header10.toBuffer();

        return Buffers.allocate(4 + SIZE + dxt10Size)
            .putInt(0x20534444) // "DDS "
            .putInt(SIZE) // size
            .putInt(flags)
            .putInt(height)
            .putInt(width)
            .putInt(pitchOrLinearSize)
            .putInt(depth)
            .putInt(mipMapCount)
            .position(0x4c) // skip 11 ints
            .put(pixelFormat.toBuffer())
            .putInt(caps1)
            .putInt(caps2)
            .putInt(0) // caps3
            .putInt(0) // caps4
            .putInt(0) // reserved
            .put(dxt10Buffer)
            .flip();
    }
}
