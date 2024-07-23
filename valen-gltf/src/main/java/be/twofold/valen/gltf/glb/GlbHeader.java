package be.twofold.valen.gltf.glb;

import java.nio.*;

public record GlbHeader(
    int version,
    int length
) {
    public static final int BYTES = 12;

    public static GlbHeader of(int length) {
        return new GlbHeader(2, length);
    }

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(12)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(0x46546c67)
            .putInt(version)
            .putInt(length)
            .flip();
    }
}
