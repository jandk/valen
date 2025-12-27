package be.twofold.valen.format.granite.util;

import wtf.reversed.toolbox.collect.*;

import java.time.*;
import java.util.*;

public final class DotNetUtils {
    private static final Instant DOTNET_EPOCH = Instant.parse("0001-01-01T00:00:00Z");

    private DotNetUtils() {
    }

    public static Instant ticksToInstant(long ticks) {
        return DOTNET_EPOCH
            .plusSeconds(ticks / 10_000_000)
            .plusNanos((ticks % 10_000_000) * 100);
    }

    public static long instantToTicks(Instant instant) {
        var d = Duration.between(DOTNET_EPOCH, instant);
        return d.getSeconds() * 10_000_000 + d.getNano() / 100;
    }

    public static UUID guidBytesToUUID(Bytes bytes) {
        var p1 = Integer.toUnsignedLong(bytes.getInt(0));
        var p2 = Short.toUnsignedLong(bytes.getShort(4));
        var p3 = Short.toUnsignedLong(bytes.getShort(6));

        var high = (p1 << 32) | (p2 << 16) | p3;
        var low = Long.reverseBytes(bytes.getLong(8));
        return new UUID(high, low);
    }

    public static Bytes uuidToGuidBytes(UUID uuid) {
        var high = uuid.getMostSignificantBits();
        var low = uuid.getLeastSignificantBits();

        return Bytes.Mutable.allocate(16)
            .setInt(0, (int) (high >> 32))
            .setShort(4, (short) (high >> 16))
            .setShort(6, (short) high)
            .setLong(8, Long.reverseBytes(low));
    }
}
