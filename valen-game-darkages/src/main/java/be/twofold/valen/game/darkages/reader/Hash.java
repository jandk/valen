package be.twofold.valen.game.darkages.reader;

public final class Hash {
    private static final long M1 = 0xFF51_AFD7_ED55_8CCDL;
    private static final long M2 = 0xC4CE_B9FE_1A85_EC53L;
    private static final long GOLDEN = 0x9E37_79B9L;

    public static long hash(long seed, int a, int b) {
        long aa = Integer.toUnsignedLong(a);
        long bb = Integer.toUnsignedLong(b);
        return hashCombine(seed, hashCombine(fmix64(aa), fmix64(bb)));
    }

    // MurmurHash3 fmix64
    private static long fmix64(long k) {
        k ^= k >>> 33;
        k *= M1;
        k ^= k >>> 33;
        k *= M2;
        k ^= k >>> 33;
        return k;
    }

    // boot::hash_combine
    private static long hashCombine(long seed, long value) {
        return seed ^ (value + GOLDEN + (seed << 6) + (seed >>> 2));
    }
}
