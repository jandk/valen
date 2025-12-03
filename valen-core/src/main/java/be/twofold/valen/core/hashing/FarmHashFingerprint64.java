package be.twofold.valen.core.hashing;

import be.twofold.valen.core.util.collect.*;

final class FarmHashFingerprint64 implements HashFunction {
    public static final FarmHashFingerprint64 INSTANCE = new FarmHashFingerprint64();

    // Some primes between 2^63 and 2^64 for various uses.
    private static final long K0 = 0xC3A5C85C97CB3127L;
    private static final long K1 = 0xB492B66FBE98F273L;
    private static final long K2 = 0x9AE16A3B2F90404FL;
    private static final long SEED = 81;

    private FarmHashFingerprint64() {
    }

    @Override
    public HashCode hash(Bytes src) {
        return HashCode.ofLong(fingerprint(src));
    }

    private static long fingerprint(Bytes s) {
        int length = s.length();
        if (length <= 32) {
            if (length <= 16) {
                return hashLength0to16(s);
            } else {
                return hashLength17to32(s);
            }
        } else if (length <= 64) {
            return hashLength33to64(s);
        } else {
            return hashLength65Plus(s);
        }
    }

    private static long shiftMix(long val) {
        return val ^ (val >>> 47);
    }

    private static long hashLength16(long u, long v, long mul) {
        long a = (u ^ v) * mul;
        a ^= (a >>> 47);
        long b = (v ^ a) * mul;
        b ^= (b >>> 47);
        b *= mul;
        return b;
    }

    private static UInt128 weakHashLength32WithSeeds(Bytes s, int offset, long a, long b) {
        long w = s.getLong(offset);
        long x = s.getLong(offset + 8);
        long y = s.getLong(offset + 16);
        long z = s.getLong(offset + 24);

        a += w;
        b = Long.rotateRight(b + a + z, 21);
        long c = a;
        a += x;
        a += y;
        b += Long.rotateRight(a, 44);
        return new UInt128(a + z, b + c);
    }

    private static long hashLength0to16(Bytes s) {
        int length = s.length();
        if (length >= 8) {
            long mul = K2 + length * 2L;
            long a = s.getLong(0) + K2;
            long b = s.getLong(s.length() - 8);
            long c = Long.rotateRight(b, 37) * mul + a;
            long d = (Long.rotateRight(a, 25) + b) * mul;
            return hashLength16(c, d, mul);
        }
        if (length >= 4) {
            long mul = K2 + length * 2L;
            long a = s.getUnsignedInt(0);
            long b = s.getUnsignedInt(s.length() - 4);
            return hashLength16(length + (a << 3), b, mul);
        }
        if (length > 0) {
            int a = s.getUnsigned(0);
            int b = s.getUnsigned(length >>> 1);
            int c = s.getUnsigned(length - 1);

            int y = a + (b << 8);
            int z = length + (c << 2);
            return shiftMix(y * K2 ^ z * K0) * K2;
        }
        return K2;
    }

    private static long hashLength17to32(Bytes s) {
        int length = s.length();
        long mul = K2 + length * 2L;
        long a = s.getLong(0) * K1;
        long b = s.getLong(8);
        long c = s.getLong(length - 8) * mul;
        long d = s.getLong(length - 16) * K2;

        return hashLength16(Long.rotateRight(a + b, 43) + Long.rotateRight(c, 30) + d, a + Long.rotateRight(b + K2, 18) + c, mul);
    }

    private static long hashLength33to64(Bytes s) {
        int length = s.length();
        long mul = K2 + length * 2L;
        long a = s.getLong(0) * K2;
        long b = s.getLong(8);
        long c = s.getLong(length - 8) * mul;
        long d = s.getLong(length - 16) * K2;
        long y = Long.rotateRight(a + b, 43) + Long.rotateRight(c, 30) + d;
        long z = hashLength16(y, a + Long.rotateRight(b + K2, 18) + c, mul);
        long e = s.getLong(16) * mul;
        long f = s.getLong(24);
        long g = (y + s.getLong(length - 32)) * mul;
        long h = (z + s.getLong(length - 24)) * mul;

        return hashLength16(Long.rotateRight(e + f, 43) + Long.rotateRight(g, 30) + h, e + Long.rotateRight(f + a, 18) + g, mul);
    }

    private static long hashLength65Plus(Bytes s) {
        // For strings over 64 bytes we loop.  Internal state consists of
        // 56 bytes: v, w, x, y, and z.
        int length = s.length();

        long x = SEED;
        @SuppressWarnings("NumericOverflow")
        long y = SEED * K1 + 113;
        long z = shiftMix(y * K2 + 113) * K2;
        UInt128 v = new UInt128(0, 0);
        UInt128 w = new UInt128(0, 0);
        x = x * K2 + s.getLong(0);

        int offset = 0;
        do {
            x = Long.rotateRight(x + y + v.first + s.getLong(offset + 8), 37) * K1;
            y = Long.rotateRight(y + v.second + s.getLong(offset + 48), 42) * K1;
            x ^= w.second;
            y += v.first + s.getLong(offset + 40);
            z = Long.rotateRight(z + w.first, 33) * K1;
            long t = s.getLong(offset + 16);
            v = weakHashLength32WithSeeds(s, offset, v.second * K1, x + w.first);
            w = weakHashLength32WithSeeds(s, offset + 32, z + w.second, y + t);
            t = x;
            x = z;
            z = t;
            offset += 64;
        } while (offset + 64 < length);

        long mul = K1 + ((z & 0xff) << 1);
        // Make offset point to the last 64 bytes of input.
        offset = s.length() - 64;
        w.first += ((length - 1) & 63);
        v.first += w.first;
        w.first += v.first;
        x = Long.rotateRight(x + y + v.first + s.getLong(offset + 8), 37) * mul;
        y = Long.rotateRight(y + v.second + s.getLong(offset + 48), 42) * mul;
        x ^= w.second * 9;
        y += v.first * 9 + s.getLong(offset + 40);
        z = Long.rotateRight(z + w.first, 33) * mul;
        long t = s.getLong(offset + 16);
        v = weakHashLength32WithSeeds(s, offset, v.second * mul, x + w.first);
        w = weakHashLength32WithSeeds(s, offset + 32, z + w.second, y + t);
        return hashLength16(hashLength16(v.first, w.first, mul) + shiftMix(y) * K0 + x, hashLength16(v.second, w.second, mul) + z, mul);
    }

    private static final class UInt128 {
        private long first;
        private long second;

        private UInt128(long first, long second) {
            this.first = first;
            this.second = second;
        }
    }
}
