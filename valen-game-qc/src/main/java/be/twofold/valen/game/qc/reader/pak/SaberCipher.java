package be.twofold.valen.game.qc.reader.pak;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

/**
 * Saber's stream cipher, as shipped in QuakeChampions.exe.
 * <p>
 * It is a self-synchronizing cipher over a 32-byte feedback buffer, seeded
 * from the 32-byte key stored in the pak's trailer, with an [Nr3Random]
 * keystream layered on top.
 */
final class SaberCipher {
    private final byte[] feedback;
    private final Nr3Random random;

    private long seed;
    private int sdIndex;
    private int fbIndex;

    SaberCipher(Bytes key) {
        Check.argument(key.length() == 32, "Key must be 32 bytes");
        this.feedback = key.toArray();

        this.seed = key.getLong(0);
        this.random = new Nr3Random(seed ^ 0xCEED);
    }

    void decrypt(Bytes.Mutable bytes) {
        for (int i = 0; i < bytes.length(); i++) {
            byte prev = feedback[fbIndex];
            byte curr = bytes.get(i);
            feedback[fbIndex] = curr;

            byte keyByte = sdIndex == 0 ? (byte) seed : 0;
            bytes.set(i, (byte) (curr ^ prev ^ keyByte));

            fbIndex = (fbIndex + 1) & 0x1F;
            if (++sdIndex == 8) {
                seed = random.int64();
                sdIndex = 0;
            }
        }
    }

    /**
     * The {@code Ran} combined generator from <b>Numerical Recipes</b>, 3rd edition (§7.1.1).
     */
    static final class Nr3Random {
        private long u;
        private long v = 0x38EC_AC5F_B325_1641L;
        private long w = 1;

        Nr3Random(long seed) {
            u = v ^ seed;
            int64();
            v = u;
            int64();
            w = v;
            int64();
        }

        long int64() {
            u = u * 2862933555777941757L + 7046029254386353087L;

            v ^= v >>> 17;
            v ^= v << 31;
            v ^= v >>> 8;

            w = 4294957665L * (w & 0xFFFF_FFFFL) + (w >>> 32);

            long x = u ^ (u << 21);
            x ^= x >>> 35;
            x ^= x << 4;

            return (x + v) ^ w;
        }
    }
}
