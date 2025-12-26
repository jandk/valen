package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import be.twofold.valen.core.io.*;

import java.io.*;

public record BlangEntry(
    int hash,
    String key,
    String value,
    String feature
) {
    public static BlangEntry read(BinarySource source) throws IOException {
        var hash = source.readInt();
        var key = source.readString(StringFormat.INT_LENGTH);
        var value = source.readString(StringFormat.INT_LENGTH);
        var feature = source.readString(StringFormat.INT_LENGTH);
        return new BlangEntry(hash, key, value, feature);
    }
}
