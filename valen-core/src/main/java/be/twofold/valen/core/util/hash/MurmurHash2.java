package be.twofold.valen.core.util.hash;

import be.twofold.valen.core.util.*;

@SuppressWarnings("PointlessArithmeticExpression")
public final class MurmurHash2 {
    private static final int M32 = 0x5bd1e995;
    private static final int R32 = 24;

    private MurmurHash2() {
    }

    public static long hash64B(byte[] array, int fromIndex, int toIndex, long seed) {
        Check.fromToIndex(fromIndex, toIndex, array.length);

        int h1 = (int) (seed) ^ toIndex - fromIndex;
        int h2 = (int) (seed >>> 32);

        int offset = fromIndex;
        while (offset <= toIndex - 8) {
            h1 = round(array, offset + 0, h1);
            h2 = round(array, offset + 4, h2);
            offset += 8;
        }

        if (offset <= toIndex - 4) {
            h1 = round(array, offset, h1);
            offset += 4;
        }

        switch (toIndex - offset) {
            case 3:
                h2 ^= Byte.toUnsignedInt(array[offset + 2]) << 16;
            case 2:
                h2 ^= Byte.toUnsignedInt(array[offset + 1]) << 8;
            case 1:
                h2 ^= Byte.toUnsignedInt(array[offset + 0]);
                h2 *= M32;
        }

        h1 = (h1 ^ (h2 >>> 18)) * M32;
        h2 = (h2 ^ (h1 >>> 22)) * M32;
        h1 = (h1 ^ (h2 >>> 17)) * M32;
        h2 = (h2 ^ (h1 >>> 19)) * M32;

        long l1 = Integer.toUnsignedLong(h1);
        long l2 = Integer.toUnsignedLong(h2);
        return (l1 << 32) | l2;
    }

    private static int round(byte[] array, int offset, int h) {
        int k = getIntLE(array, offset);
        k *= M32;
        k ^= k >>> R32;
        k *= M32;
        h *= M32;
        h ^= k;
        return h;
    }

    private static int getIntLE(byte[] data, int index) {
        return ByteArrays.getInt(data, index);
    }
}
