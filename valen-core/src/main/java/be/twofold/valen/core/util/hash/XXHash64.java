package be.twofold.valen.core.util.hash;

import be.twofold.valen.core.util.*;

public final class XXHash64 {
    private static final long PRIME64_1 = 0x9E3779B185EBCA87L;
    private static final long PRIME64_2 = 0xC2B2AE3D27D4EB4FL;
    private static final long PRIME64_3 = 0x165667B19E3779F9L;
    private static final long PRIME64_4 = 0x85EBCA77C2B2AE63L;
    private static final long PRIME64_5 = 0x27D4EB2F165667C5L;

    private XXHash64() {
    }

    public static long hash(byte[] array, int offset, int length, long seed) {
        Check.fromIndexSize(offset, length, array.length);

        long acc;
        if (offset <= length - 32) {
            // Step 1: Initialize internal accumulators
            long acc1 = seed + PRIME64_1 + PRIME64_2;
            long acc2 = seed + PRIME64_2;
            long acc3 = seed + 0;
            long acc4 = seed - PRIME64_1;

            // Step 2: Process stripes
            do {
                acc1 = round(acc1, ByteArrays.getLong(array, offset));
                offset += 8;
                acc2 = round(acc2, ByteArrays.getLong(array, offset));
                offset += 8;
                acc3 = round(acc3, ByteArrays.getLong(array, offset));
                offset += 8;
                acc4 = round(acc4, ByteArrays.getLong(array, offset));
                offset += 8;
            } while (offset <= length - 32);

            // Step 3: Accumulator convergence
            acc = Long.rotateLeft(acc1, 1)
                + Long.rotateLeft(acc2, 7)
                + Long.rotateLeft(acc3, 12)
                + Long.rotateLeft(acc4, 18);

            acc = mergeAccumulator(acc, acc1);
            acc = mergeAccumulator(acc, acc2);
            acc = mergeAccumulator(acc, acc3);
            acc = mergeAccumulator(acc, acc4);
        } else {
            // Special case: input is less than 32 bytes
            acc = seed + PRIME64_5;
        }

        // Step 4: Add input length
        acc = acc + length;

        // Step 5: Consume remaining input
        while (length - offset >= 8) {
            long lane = ByteArrays.getLong(array, offset);
            acc = acc ^ round(0, lane);
            acc = Long.rotateLeft(acc, 27) * PRIME64_1;
            acc = acc + PRIME64_4;
            offset += 8;
        }

        if (length - offset >= 4) {
            long lane = Integer.toUnsignedLong(ByteArrays.getInt(array, offset));
            acc = acc ^ (lane * PRIME64_1);
            acc = Long.rotateLeft(acc, 23) * PRIME64_2;
            acc = acc + PRIME64_3;
            offset += 4;
        }

        while (length - offset >= 1) {
            long lane = Byte.toUnsignedLong(array[offset]);
            acc = acc ^ (lane * PRIME64_5);
            acc = Long.rotateLeft(acc, 11) * PRIME64_1;
            offset += 1;
        }

        // Step 6: Final mix (avalanche)
        acc = acc ^ (acc >>> 33);
        acc = acc * PRIME64_2;
        acc = acc ^ (acc >>> 29);
        acc = acc * PRIME64_3;
        acc = acc ^ (acc >>> 32);
        return acc;
    }

    private static long round(long accN, long laneN) {
        accN = accN + (laneN * PRIME64_2);
        accN = Long.rotateLeft(accN, 31);
        return accN * PRIME64_1;
    }

    private static long mergeAccumulator(long acc, long accN) {
        acc = acc ^ round(0, accN);
        acc = acc * PRIME64_1;
        return acc + PRIME64_4;
    }
}
