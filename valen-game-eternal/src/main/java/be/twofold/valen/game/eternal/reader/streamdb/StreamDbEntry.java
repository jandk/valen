package be.twofold.valen.game.eternal.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbEntry(
    long identity,
    int offset16,
    int length
) {
    public static StreamDbEntry read(BinarySource source) throws IOException {
        var identity = source.readLong();
        var offset16 = source.readInt();
        var length = source.readInt();
        return new StreamDbEntry(identity, offset16, length);
    }

    public long offset() {
        return offset16 * 16L;
    }
}
