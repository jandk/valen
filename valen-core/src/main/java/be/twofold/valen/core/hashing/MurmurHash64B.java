package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;

record MurmurHash64B(long seed) implements HashFunction {
    private static final int M32 = 0x5BD1E995;
    private static final int R32 = 24;

    @Override
    public HashCode hash(Bytes src) {
        var length = src.size();
        var offset = 0;

        int h1 = (int) (seed) ^ length;
        int h2 = (int) (seed >>> 32);

        while (offset + 8 <= length) {
            h1 = round(h1, src.getInt(offset));
            h2 = round(h2, src.getInt(offset + 4));
            offset += 8;
        }

        if (offset + 4 <= length) {
            h1 = round(h1, src.getInt(offset));
            offset += 4;
        }

        switch (length - offset) {
            case 3:
                h2 ^= src.getUnsignedByte(offset + 2) << 16;
            case 2:
                h2 ^= src.getUnsignedByte(offset + 1) << 8;
            case 1:
                h2 ^= src.getUnsignedByte(offset);
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
