package be.twofold.valen.game.darkages.reader.image;

import java.nio.*;

public final class Hash {
    static long hash(ByteBuffer key) {
        long c1 = 0xFF51AFD7ED558CCDL;
        long c2 = 0xC4CEB9FE1A85EC53L;
        long c3 = 0x000000009E3779B9L;

        var key00 = key.getLong(0);
        var key08 = c1 * Integer.toUnsignedLong(key.getInt(8));
        var key12 = c1 * Integer.toUnsignedLong(key.getInt(12));

        long p1 = mix33(key12);
        long p2 = ((c2 * mix33(key08)) >>> 33) ^ (c2 * mix33(key08));
        long p3 = key00 ^ ((key00 >>> 2) + (key00 << 6) + (p2 ^ ((p2 >>> 2) + (p2 << 6) + (mix33(c2 * p1)) + c3)) + c3);
        return p3;
    }

    private static long mix33(long l) {
        return l ^ (l >>> 33);
    }
}
