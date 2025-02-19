package be.twofold.valen.core.hashing;

import java.nio.*;

final class MurmurHash64B implements HashFunction {
    private static final int M32 = 0x5BD1E995;
    private static final int R32 = 24;

    private final long seed;

    MurmurHash64B(long seed) {
        this.seed = seed;
    }

    @Override
    public HashCode hash(ByteBuffer buffer) {
        var src = buffer.slice().order(ByteOrder.LITTLE_ENDIAN);
        var len = buffer.remaining();

        int h1 = (int) (seed) ^ len;
        int h2 = (int) (seed >>> 32);

        while (src.remaining() >= 8) {
            h1 = round(h1, src.getInt());
            h2 = round(h2, src.getInt());
        }

        if (src.remaining() >= 4) {
            h1 = round(h1, src.getInt());
        }

        switch (src.remaining()) {
            case 3:
                h2 ^= Byte.toUnsignedInt(src.get(src.position() + 2)) << 16;
            case 2:
                h2 ^= Byte.toUnsignedInt(src.get(src.position() + 1)) << 8;
            case 1:
                h2 ^= Byte.toUnsignedInt(src.get());
                h2 *= M32;
        }

        h1 = (h1 ^ (h2 >>> 18)) * M32;
        h2 = (h2 ^ (h1 >>> 22)) * M32;
        h1 = (h1 ^ (h2 >>> 17)) * M32;
        h2 = (h2 ^ (h1 >>> 19)) * M32;

        long l1 = Integer.toUnsignedLong(h1);
        long l2 = Integer.toUnsignedLong(h2);
        return HashCode.ofLong(l1 << 32 | l2);
    }

    private int round(int h, int k) {
        k *= M32;
        k ^= k >>> R32;
        k *= M32;
        h *= M32;
        h ^= k;
        return h;
    }
}
