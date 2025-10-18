package be.twofold.valen.game.darkages.reader;

public final class Hash {
    private static final long C1 = 0xFF51AFD7ED558CCDL;
    private static final long C2 = 0xC4CEB9FE1A85EC53L;
    private static final long C3 = 0x000000009E3779B9L;

    public static long hash(long hash, int param1, int param2) {
        long k0 = hash;
        long k1 = C1 * Integer.toUnsignedLong(param1);
        long k2 = C1 * Integer.toUnsignedLong(param2);

        long p1 = mix33(k2);
        long p2 = ((Hash.C2 * mix33(k1)) >>> 33) ^ (Hash.C2 * mix33(k1));
        return k0 ^ ((k0 >>> 2) + (k0 << 6) + (p2 ^ ((p2 >>> 2) + (p2 << 6) + (mix33(Hash.C2 * p1)) + Hash.C3)) + Hash.C3);
    }

    private static long mix33(long l) {
        return l ^ (l >>> 33);
    }
}
