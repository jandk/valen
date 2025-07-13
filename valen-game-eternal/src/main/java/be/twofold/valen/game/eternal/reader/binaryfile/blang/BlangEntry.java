package be.twofold.valen.game.eternal.reader.binaryfile.blang;

import be.twofold.valen.core.io.*;

import java.io.*;

public record BlangEntry(
    int hash,
    String key,
    String value,
    String feature
) {
    public static BlangEntry read(BinaryReader reader) throws IOException {
        var hash = reader.readInt();
        var key = reader.readPString();
        var value = reader.readPString();
        var feature = reader.readPString();
        return new BlangEntry(hash, key, value, feature);
    }
}
