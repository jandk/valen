package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record BlangEntry(
    int hash,
    String key,
    String value,
    String feature
) {
    public static BlangEntry read(BinaryReader reader) throws IOException {
        var hash = reader.readInt();
        var key = reader.readString(StringFormat.INT_LENGTH);
        var value = reader.readString(StringFormat.INT_LENGTH);
        var feature = reader.readString(StringFormat.INT_LENGTH);
        return new BlangEntry(hash, key, value, feature);
    }
}
