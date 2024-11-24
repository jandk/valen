package be.twofold.valen.core.hashing;

final class XXHashGenerator {
    private static final long PRIME32 = 0x000000009E3779B1L;
    private static final long PRIME64 = 0x9E3779B185EBCA8DL;

    private XXHashGenerator() {
    }

    static byte[] generate(int length) {
        byte[] buffer = new byte[length];
        long byteGen = PRIME32;
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) (byteGen >> 56);
            byteGen *= PRIME64;
        }
        return buffer;
    }
}
