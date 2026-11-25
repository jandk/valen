package be.twofold.valen.export.dds;

import wtf.reversed.toolbox.io.*;

import java.io.*;

record DdsHeaderDxt10(
    int dxgiFormat,
    int resourceDimension,
    int miscFlag,
    int arraySize,
    int miscFlags2
) {
    // dwResourceDimension
    public static final int DDS_DIMENSION_TEXTURE1D = 2;
    public static final int DDS_DIMENSION_TEXTURE2D = 3;
    public static final int DDS_DIMENSION_TEXTURE3D = 4;

    // dwMiscFlag
    public static final int DDS_RESOURCE_MISC_TEXTURECUBE = 0x4;

    // dwMiscFlags2
    public static final int DDS_ALPHA_MODE_UNKNOWN = 0x0;
    public static final int DDS_ALPHA_MODE_STRAIGHT = 0x1;
    public static final int DDS_ALPHA_MODE_PREMULTIPLIED = 0x2;
    public static final int DDS_ALPHA_MODE_OPAQUE = 0x3;
    public static final int DDS_ALPHA_MODE_CUSTOM = 0x4;

    public void writeTo(BinaryWriter writer) throws IOException {
        writer.writeInt(dxgiFormat);
        writer.writeInt(resourceDimension);
        writer.writeInt(miscFlag);
        writer.writeInt(arraySize);
        writer.writeInt(miscFlags2);
    }
}
