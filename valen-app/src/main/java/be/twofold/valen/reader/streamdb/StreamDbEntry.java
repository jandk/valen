package be.twofold.valen.reader.streamdb;

import be.twofold.valen.core.util.*;

public record StreamDbEntry(
    long identity,
    int offset16,
    int length
) {
    public static final int BYTES = 16;

    public static StreamDbEntry read(BetterBuffer buffer) {
        var identity = buffer.getLong();
        var offset16 = buffer.getInt();
        var length = buffer.getInt();
        return new StreamDbEntry(identity, offset16, length);
    }

    public long offset() {
        return offset16 * 16L;
    }
}
