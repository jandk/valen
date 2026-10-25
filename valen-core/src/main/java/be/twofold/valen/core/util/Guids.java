package be.twofold.valen.core.util;

import wtf.reversed.toolbox.collect.*;

import java.util.*;

public final class Guids {
    private Guids() {
    }

    public static UUID fromBytes(Bytes bytes) {
        var p1 = Integer.toUnsignedLong(bytes.getInt(0));
        var p2 = Short.toUnsignedLong(bytes.getShort(4));
        var p3 = Short.toUnsignedLong(bytes.getShort(6));

        var high = (p1 << 32) | (p2 << 16) | p3;
        var low = Long.reverseBytes(bytes.getLong(8));
        return new UUID(high, low);
    }

    public static Bytes toBytes(UUID uuid) {
        var high = uuid.getMostSignificantBits();
        var low = uuid.getLeastSignificantBits();

        return Bytes.Mutable.allocate(16)
            .setInt(0, (int) (high >> 32))
            .setShort(4, (short) (high >> 16))
            .setShort(6, (short) high)
            .setLong(8, Long.reverseBytes(low));
    }
}
