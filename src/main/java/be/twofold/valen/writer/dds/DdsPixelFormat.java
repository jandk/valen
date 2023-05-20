package be.twofold.valen.writer.dds;

import java.nio.*;

final class DdsPixelFormat {
    int flags;
    int fourCC;
    int rgbBitCount;
    int rBitMask;
    int gBitMask;
    int bBitMask;
    int aBitMask;

    private static final int Size = 0x20;

    ByteBuffer toBuffer() {
        return ByteBuffer.allocate(Size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(Size)
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
