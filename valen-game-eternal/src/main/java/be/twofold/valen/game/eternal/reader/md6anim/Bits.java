package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.util.*;

import java.util.*;

public final class Bits {
    private final byte[] bytes;

    public Bits(byte[] bytes) {
        this.bytes = Arrays.copyOf(bytes, bytes.length);
    }

    public boolean get(int index) {
        Check.index(index, size());

        int byteIndex = index >> 3;
        int bitIndex = 0x80 >> (index & 7);
        return (bytes[byteIndex] & bitIndex) != 0;
    }

    public int size() {
        return bytes.length * 8;
    }

    public int cardinality() {
        int count = 0;
        for (byte b : bytes) {
            count += Integer.bitCount(Byte.toUnsignedInt(b));
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bits other
               && Arrays.equals(bytes, other.bytes);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(bytes);
    }
}
