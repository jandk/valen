package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;

final class XXHashGenerator {
    private static final long PRIME32 = 0x000000009E3779B1L;
    private static final long PRIME64 = 0x9E3779B185EBCA8DL;

    private XXHashGenerator() {
    }

    static Bytes generate(int length) {
        Bytes.Mutable bytes = Bytes.Mutable.allocate(length);
        long byteGen = PRIME32;
        for (int i = 0; i < length; i++) {
            bytes.set(i, (byte) (byteGen >> 56));
            byteGen *= PRIME64;
        }
        return bytes;
    }
}
