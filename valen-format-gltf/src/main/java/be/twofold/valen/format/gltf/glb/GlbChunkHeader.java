package be.twofold.valen.format.gltf.glb;

import java.nio.*;

public record GlbChunkHeader(
    int length,
    GlbChunkType type
) {
    public static final int BYTES = 8;

    public GlbChunkHeader {
        if (length < 0) {
            throw new IllegalArgumentException("length < 0");
        }
    }

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(BYTES)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(length)
            .putInt(type.value())
            .flip();
    }
}
