package be.twofold.valen.reader.blang;

import be.twofold.valen.core.util.*;

public record BlangEntry(
    int hash,
    String key,
    String value
) {
    public static BlangEntry read(BetterBuffer buffer) {
        var hash = buffer.getInt();
        var key = buffer.getString();
        var value = buffer.getString();
        return new BlangEntry(hash, key, value);
    }
}
