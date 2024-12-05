package be.twofold.valen.gltf.glb;

import java.nio.*;

public record GlbHeader(
    int length
) {
    private static final int MAGIC = 0x46546C67;
    private static final int VERSION = 2;

    public static final int BYTES = 12;

    public GlbHeader {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
    }

    public static GlbHeader of(int length) {
        return new GlbHeader(length);
    }

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(BYTES)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(MAGIC)
            .putInt(VERSION)
            .putInt(length)
            .flip();
    }
}
