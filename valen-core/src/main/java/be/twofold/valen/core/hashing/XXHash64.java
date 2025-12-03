package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;

record XXHash64(long seed) implements HashFunction {
    private static final long PRIME64_1 = 0x9E3779B185EBCA87L;
    private static final long PRIME64_2 = 0xC2B2AE3D27D4EB4FL;
    private static final long PRIME64_3 = 0x165667B19E3779F9L;
    private static final long PRIME64_4 = 0x85EBCA77C2B2AE63L;
    private static final long PRIME64_5 = 0x27D4EB2F165667C5L;

    @Override
    public HashCode hash(Bytes src) {
        var length = src.length();
        var offset = 0;

        long acc;
        if (offset + 32 <= length) {
            // Step 1: Initialize internal accumulators
            long acc1 = seed + PRIME64_1 + PRIME64_2;
            long acc2 = seed + PRIME64_2;
            long acc3 = seed;
            long acc4 = seed - PRIME64_1;

            // Step 2: Process stripes
            do {
                acc1 = round(acc1, src.getLong(offset));
                acc2 = round(acc2, src.getLong(offset + 8));
                acc3 = round(acc3, src.getLong(offset + 16));
                acc4 = round(acc4, src.getLong(offset + 24));
                offset += 32;
            } while (offset + 32 <= length);

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
        while (offset + 8 <= length) {
            long lane = src.getLong(offset);
            acc = acc ^ round(0, lane);
            acc = Long.rotateLeft(acc, 27) * PRIME64_1;
            acc = acc + PRIME64_4;
            offset += 8;
        }

        if (offset + 4 <= length) {
            long lane = src.getUnsignedInt(offset);
            acc = acc ^ (lane * PRIME64_1);
            acc = Long.rotateLeft(acc, 23) * PRIME64_2;
            acc = acc + PRIME64_3;
            offset += 4;
        }

        while (offset + 1 <= length) {
            long lane = src.getUnsigned(offset);
            acc = acc ^ (lane * PRIME64_5);
            acc = Long.rotateLeft(acc, 11) * PRIME64_1;
            offset++;
        }

        // Step 6: Final mix (avalanche)
        acc = acc ^ (acc >>> 33);
        acc = acc * PRIME64_2;
        acc = acc ^ (acc >>> 29);
        acc = acc * PRIME64_3;
        acc = acc ^ (acc >>> 32);

        return HashCode.ofLong(acc);
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
