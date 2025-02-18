package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.*;

final class XXHash32 implements HashFunction {
    private static final int PRIME32_1 = 0x9E3779B1;
    private static final int PRIME32_2 = 0x85EBCA77;
    private static final int PRIME32_3 = 0xC2B2AE3D;
    private static final int PRIME32_4 = 0x27D4EB2F;
    private static final int PRIME32_5 = 0x165667B1;

    private final int seed;

    XXHash32(int seed) {
        this.seed = seed;
    }

    @Override
    public HashCode hash(byte[] array, int offset, int length) {
        Check.fromIndexSize(offset, length, array.length);
        int limit = offset + length;

        int acc;
        if (offset <= limit - 16) {
            // Step 1: Initialize internal accumulators
            int acc1 = seed + PRIME32_1 + PRIME32_2;
            int acc2 = seed + PRIME32_2;
            int acc3 = seed;
            int acc4 = seed - PRIME32_1;

            // Step 2: Process stripes
            do {
                acc1 = round(acc1, ByteArrays.getInt(array, offset));
                acc2 = round(acc2, ByteArrays.getInt(array, offset + 4));
                acc3 = round(acc3, ByteArrays.getInt(array, offset + 8));
                acc4 = round(acc4, ByteArrays.getInt(array, offset + 12));
                offset += 16;
            } while (offset <= limit - 16);

            // Step 3: Accumulator convergence
            acc = Integer.rotateLeft(acc1, 1)
                + Integer.rotateLeft(acc2, 7)
                + Integer.rotateLeft(acc3, 12)
                + Integer.rotateLeft(acc4, 18);
        } else {
            // Special case: input is less than 16 bytes
            acc = seed + PRIME32_5;
        }

        // Step 4: Add input length
        acc = acc + length;

        // Step 5: Consume remaining input
        while (offset <= limit - 4) {
            int lane = ByteArrays.getInt(array, offset);
            acc = acc + (lane * PRIME32_3);
            acc = Integer.rotateLeft(acc, 17) * PRIME32_4;
            offset += 4;
        }

        while (offset < limit) {
            int lane = Byte.toUnsignedInt(array[offset]);
            acc = acc + (lane * PRIME32_5);
            acc = Integer.rotateLeft(acc, 11) * PRIME32_1;
            offset++;
        }

        // Step 6: Final mix (avalanche)
        acc = acc ^ (acc >>> 15);
        acc = acc * PRIME32_2;
        acc = acc ^ (acc >>> 13);
        acc = acc * PRIME32_3;
        acc = acc ^ (acc >>> 16);

        return HashCode.ofInt(acc);
    }

    private static int round(int acc, int lane) {
        acc = acc + (lane * PRIME32_2);
        acc = Integer.rotateLeft(acc, 13);
        return acc * PRIME32_1;
    }
}
