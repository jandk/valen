package be.twofold.valen.core.hashing;

@FunctionalInterface
public interface HashFunction {
    static HashFunction farmHashFingerprint64() {
        return new FarmHashFingerprint64();
    }

    static HashFunction murmurHash64B(long seed) {
        return new MurmurHash64B(seed);
    }

    static HashFunction xxHash32(int seed) {
        return new XXHash32(seed);
    }

    static HashFunction xxHash64(long seed) {
        return new XXHash64(seed);
    }

    default HashCode hash(byte[] array) {
        return hash(array, 0, array.length);
    }

    HashCode hash(byte[] array, int offset, int length);
}
