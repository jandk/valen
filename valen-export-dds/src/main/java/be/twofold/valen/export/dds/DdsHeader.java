package be.twofold.valen.export.dds;

import wtf.reversed.toolbox.io.*;

import java.io.*;

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

    public void writeTo(BinaryWriter writer) throws IOException {
        writer.writeInt(0x20534444); // "DDS "
        writer.writeInt(SIZE); // size
        writer.writeInt(flags);
        writer.writeInt(height);
        writer.writeInt(width);
        writer.writeInt(pitchOrLinearSize);
        writer.writeInt(depth);
        writer.writeInt(mipMapCount);
        writer.writeZeros(11 * Integer.BYTES); // reserved
        pixelFormat.writeTo(writer);
        writer.writeInt(caps1);
        writer.writeInt(caps2);
        writer.writeInt(0); // caps3
        writer.writeInt(0); // caps4
        writer.writeInt(0); // reserved
        if (header10 != null) {
            header10.writeTo(writer);
        }
    }
}
