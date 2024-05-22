package be.twofold.valen.export.gltf.glb;

import java.nio.*;

public record GlbHeader(
    int version,
    int length
) {
    private static final int Magic = 0x46546C67;

    public static GlbHeader of(int length) {
        return new GlbHeader(2, length);
    }

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(12)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(Magic)
            .putInt(version)
            .putInt(length)
            .flip();
    }
}
