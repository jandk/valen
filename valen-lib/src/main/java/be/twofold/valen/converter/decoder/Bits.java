package be.twofold.valen.converter.decoder;

final class Bits {
    private long lo;
    private long hi;

    private Bits(long lo, long hi) {
        this.lo = lo;
        this.hi = hi;
    }

    static Bits from(byte[] array, int index) {
        var lo = (long) BcUtils.LongVarHandle.get(array, index);
        var hi = (long) BcUtils.LongVarHandle.get(array, index + 8);
        return new Bits(lo, hi);
    }

    int getBits(int count) {
        int mask = (1 << count) - 1;
        int bits = (int) (lo & mask);
        lo = (lo >>> count) | ((hi & mask) << (64 - count));
        hi >>>= count;
        return bits;
    }

    int getBit() {
        return getBits(1);
    }
}
