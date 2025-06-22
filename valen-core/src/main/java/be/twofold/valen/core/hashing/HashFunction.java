package be.twofold.valen.core.hashing;

import java.nio.*;
import java.nio.charset.*;

@FunctionalInterface
public interface HashFunction {
    static HashFunction farmHashFingerprint64() {
        return FarmHashFingerprint64.INSTANCE;
    }

    static HashFunction murmurHash64B(long seed) {
        return new MurmurHash64B(seed);
    }

    static HashFunction xxHash32(int seed) {
        return new XXHash32(seed);
    }

    static HashFunction xxHash64(long seed) {
        return new XXHash64(seed);
    }

    default HashCode hash(String string) {
        return hash(ByteBuffer.wrap(string.getBytes(StandardCharsets.UTF_8)));
    }

    HashCode hash(ByteBuffer src);
}
