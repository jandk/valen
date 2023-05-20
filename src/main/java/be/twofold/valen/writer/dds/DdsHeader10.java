package be.twofold.valen.writer.dds;

import java.nio.*;

final class DdsHeader10 {
    DxgiFormat dxgiFormat;
    int resourceDimension;
    int miscFlag;
    int arraySize;
    int miscFlags2;

    static final int Size = 0x14;

    ByteBuffer toBuffer() {
        return ByteBuffer.allocate(Size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(dxgiFormat.getCode())
            .putInt(resourceDimension)
            .putInt(miscFlag)
            .putInt(arraySize)
            .putInt(miscFlags2)
            .flip();
    }
}
