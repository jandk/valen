package be.twofold.valen.gltf.glb;

import java.nio.*;

public record GlbChunkHeader(
    int length,
    GlbChunkType type
) {
    public static final int BYTES = 8;

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(length)
            .putInt(type.value())
            .flip();
    }
}
