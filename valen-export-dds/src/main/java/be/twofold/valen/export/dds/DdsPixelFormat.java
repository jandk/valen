package be.twofold.valen.export.dds;

import wtf.reversed.toolbox.util.*;

import java.nio.*;
import java.util.*;

record DdsPixelFormat(
    Set<DdsPixelFormatFlags> flags,
    DdsPixelFormatFourCC fourCC,
    int rgbBitCount,
    int rBitMask,
    int gBitMask,
    int bBitMask,
    int aBitMask
) {
    public static final int SIZE = 32;

    DdsPixelFormat {
        flags = Set.copyOf(flags);
        Objects.requireNonNull(fourCC);
    }

    public static DdsPixelFormat fromBuffer(ByteBuffer buffer) throws DdsException {
        if (buffer.getInt() != SIZE) {
            throw new DdsException("Invalid DdsPixelFormat size");
        }

        var flags = DdsPixelFormatFlags.fromValue(buffer.getInt());
        var fourCC = DdsPixelFormatFourCC.fromValue(buffer.getInt());
        var rgbBitCount = buffer.getInt();
        var rBitMask = buffer.getInt();
        var gBitMask = buffer.getInt();
        var bBitMask = buffer.getInt();
        var aBitMask = buffer.getInt();

        return new DdsPixelFormat(
            flags,
            fourCC,
            rgbBitCount,
            rBitMask,
            gBitMask,
            bBitMask,
            aBitMask
        );
    }

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(SIZE)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(SIZE)
            .putInt(FlagEnum.toValue(flags))
            .putInt(fourCC.getValue())
            .putInt(rgbBitCount)
            .putInt(rBitMask)
            .putInt(gBitMask)
            .putInt(bBitMask)
            .putInt(aBitMask)
            .flip();
    }
}
