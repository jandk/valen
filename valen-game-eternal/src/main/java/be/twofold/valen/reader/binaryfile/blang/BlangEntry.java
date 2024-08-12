package be.twofold.valen.reader.binaryfile.blang;

import be.twofold.valen.core.io.*;

import java.io.*;

public record BlangEntry(
    int hash,
    String key,
    String value,
    String feature
) {
    public static BlangEntry read(DataSource source) throws IOException {
        var hash = source.readInt();
        var key = source.readPString();
        var value = source.readPString();
        var feature = source.readPString();
        return new BlangEntry(hash, key, value, feature);
    }
}
