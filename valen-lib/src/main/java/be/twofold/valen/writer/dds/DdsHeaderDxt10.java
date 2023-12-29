package be.twofold.valen.writer.dds;

import java.nio.*;

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

    public static final int SIZE = 20;

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(dxgiFormat)
            .putInt(resourceDimension)
            .putInt(miscFlag)
            .putInt(arraySize)
            .putInt(miscFlags2)
            .flip();
    }
}
