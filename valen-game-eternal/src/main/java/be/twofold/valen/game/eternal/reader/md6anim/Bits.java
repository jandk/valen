package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.util.*;
import be.twofold.valen.core.util.collect.*;

import java.util.*;

public final class Bits {
    private final Bytes bytes;

    public Bits(Bytes bytes) {
        this.bytes = Objects.requireNonNull(bytes);
    }

    public boolean get(int index) {
        Check.index(index, size());

        int byteIndex = index >> 3;
        int bitIndex = 0x80 >> (index & 7);
        return (bytes.getByte(byteIndex) & bitIndex) != 0;
    }

    public int size() {
        return bytes.size() * 8;
    }

    public int cardinality() {
        int count = 0;
        for (int i = 0; i < bytes.size(); i++) {
            count += Integer.bitCount(Byte.toUnsignedInt(bytes.getByte(i)));
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Bits other
            && bytes.equals(other.bytes);
    }

    @Override
    public int hashCode() {
        return bytes.hashCode();
    }
}
