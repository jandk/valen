package be.twofold.valen.export.gltf.glb;

import java.nio.*;

public record GlbChunkHeader(
    int length,
    GlbChunkType type
) {
    public static GlbChunkHeader of(GlbChunkType type, int length) {
        return new GlbChunkHeader(length, type);
    }

    public ByteBuffer toBuffer() {
        return ByteBuffer.allocate(8)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(length)
            .putInt(type.value())
            .flip();
    }
}
