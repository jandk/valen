package be.twofold.valen.reader.blang;

import be.twofold.valen.core.util.*;

public record BlangEntry(
    int hash,
    String key,
    String value,
    String feature
) {
    public static BlangEntry read(BetterBuffer buffer) {
        var hash = buffer.getInt();
        var key = buffer.getString();
        var value = buffer.getString();
        var feature = buffer.getString();
        return new BlangEntry(hash, key, value, feature);
    }
}
