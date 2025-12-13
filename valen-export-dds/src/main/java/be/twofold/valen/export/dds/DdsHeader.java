package be.twofold.valen.export.dds;

import be.twofold.valen.core.util.*;

import java.nio.*;
import java.util.*;

public record DdsHeader(
    Set<DdsHeaderFlags> flags,
    int height,
    int width,
    int pitchOrLinearSize,
    int depth,
    int mipMapCount,
    DdsPixelFormat pixelFormat,
    Set<DdsHeaderCaps1> caps1,
    Set<DdsHeaderCaps2> caps2,
    Optional<DdsHeaderDxt10> header10
) {
    public static final int SIZE = 124;
    public static final int MAGIC = 0x20534444;

    public DdsHeader {
        flags = Set.copyOf(flags);
        Objects.requireNonNull(pixelFormat);
        caps1 = Set.copyOf(caps1);
        caps2 = Set.copyOf(caps2);
    }

    public static DdsHeader fromBuffer(ByteBuffer buffer) throws DdsException {
        if (buffer.getInt() != MAGIC) {
            throw new DdsException("Invalid magic");
        }
        if (buffer.getInt() != SIZE) {
            throw new DdsException("Invalid size");
        }
        var flags = DdsHeaderFlags.fromValue(buffer.getInt());
        var height = buffer.getInt();
        var width = buffer.getInt();
        var pitchOrLinearSize = buffer.getInt();
        var depth = buffer.getInt();
        var mipMapCount = buffer.getInt();
        buffer.position(buffer.position() + 44);
        var pixelFormat = DdsPixelFormat.fromBuffer(buffer);
        var caps1 = DdsHeaderCaps1.fromValue(buffer.getInt());
        var caps2 = DdsHeaderCaps2.fromValue(buffer.getInt());
        buffer.position(buffer.position() + 12);
        var header10 = pixelFormat.fourCC() == DdsPixelFormatFourCC.DX10 ? DdsHeaderDxt10.fromBuffer(buffer) : null;

        return new DdsHeader(
            flags,
            height,
            width,
            pitchOrLinearSize,
            depth,
            mipMapCount,
            pixelFormat,
            caps1,
            caps2,
            Optional.ofNullable(header10)
        );
    }

    public ByteBuffer toBuffer() {
        var dxt10Buffer = header10
            .map(DdsHeaderDxt10::toBuffer)
            .orElseGet(() -> ByteBuffer.allocate(0));

        return ByteBuffer.allocate(4 + SIZE + dxt10Buffer.capacity())
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(MAGIC) // "DDS "
            .putInt(SIZE) // size
            .putInt(FlagEnum.toValue(flags))
            .putInt(height)
            .putInt(width)
            .putInt(pitchOrLinearSize)
            .putInt(depth)
            .putInt(mipMapCount)
            .position(0x4c) // skip 11 ints
            .put(pixelFormat.toBuffer())
            .putInt(FlagEnum.toValue(caps1))
            .putInt(FlagEnum.toValue(caps2))
            .putInt(0) // caps3
            .putInt(0) // caps4
            .putInt(0) // reserved
            .put(dxt10Buffer)
            .flip();
    }
}
