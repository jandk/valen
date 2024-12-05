package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.*;

final class MurmurHash64B implements HashFunction {
    private static final int M32 = 0x5bd1e995;
    private static final int R32 = 24;

    private final long seed;

    MurmurHash64B(long seed) {
        this.seed = seed;
    }

    @Override
    public HashCode hash(byte[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        int limit = offset + length;

        int h1 = (int) (seed) ^ length;
        int h2 = (int) (seed >>> 32);

        while (offset <= limit - 8) {
            h1 = round(h1, ByteArrays.getInt(array, offset));
            offset += 4;
            h2 = round(h2, ByteArrays.getInt(array, offset));
            offset += 4;
        }

        if (offset <= limit - 4) {
            h1 = round(h1, ByteArrays.getInt(array, offset));
            offset += 4;
        }

        switch (limit - offset) {
            case 3:
                h2 ^= Byte.toUnsignedInt(array[offset + 2]) << 16;
            case 2:
                h2 ^= Byte.toUnsignedInt(array[offset + 1]) << 8;
            case 1:
                h2 ^= Byte.toUnsignedInt(array[offset]);
                h2 *= M32;
        }

        h1 = (h1 ^ (h2 >>> 18)) * M32;
        h2 = (h2 ^ (h1 >>> 22)) * M32;
        h1 = (h1 ^ (h2 >>> 17)) * M32;
        h2 = (h2 ^ (h1 >>> 19)) * M32;

        long l1 = Integer.toUnsignedLong(h1);
        long l2 = Integer.toUnsignedLong(h2);
        return new HashCode.LongHashCode((l1 << 32) | l2);
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
