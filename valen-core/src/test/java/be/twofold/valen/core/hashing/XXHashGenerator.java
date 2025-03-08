package be.twofold.valen.core.hashing;

import java.nio.*;

final class XXHashGenerator {
    private static final long PRIME32 = 0x000000009E3779B1L;
    private static final long PRIME64 = 0x9E3779B185EBCA8DL;

    private XXHashGenerator() {
    }

    static ByteBuffer generate(int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        long byteGen = PRIME32;
        while (buffer.hasRemaining()) {
            buffer.put((byte) (byteGen >> 56));
            byteGen *= PRIME64;
        }
        return buffer.flip();
    }
}
