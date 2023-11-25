package be.twofold.valen.writer.dds;

import java.nio.*;

public record DdsHeaderDxt10(
    int dxgiFormat,
    int resourceDimension,
    int miscFlag,
    int arraySize,
    int miscFlags2
) {
    // dwResourceDimension
    static final int DDS_DIMENSION_TEXTURE1D = 2;
    static final int DDS_DIMENSION_TEXTURE2D = 3;
    static final int DDS_DIMENSION_TEXTURE3D = 4;

    // dwMiscFlag
    static final int DDS_RESOURCE_MISC_TEXTURECUBE = 0x4;

    // dwMiscFlags2
    static final int DDS_ALPHA_MODE_UNKNOWN = 0x0;
    static final int DDS_ALPHA_MODE_STRAIGHT = 0x1;
    static final int DDS_ALPHA_MODE_PREMULTIPLIED = 0x2;
    static final int DDS_ALPHA_MODE_OPAQUE = 0x3;
    static final int DDS_ALPHA_MODE_CUSTOM = 0x4;

    static final int Size = 0x14;

    ByteBuffer toBuffer() {
        return ByteBuffer.allocate(Size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(dxgiFormat)
            .putInt(resourceDimension)
            .putInt(miscFlag)
            .putInt(arraySize)
            .putInt(miscFlags2)
            .flip();
    }
}
