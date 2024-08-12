package be.twofold.valen.game.eternal.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbPrefetchBlock(
    long name,
    int firstItemIndex,
    int numItems
) {
    public static final int BYTES = 16;

    public static StreamDbPrefetchBlock read(DataSource source) throws IOException {
        var name = source.readLong();
        var firstItemIndex = source.readInt();
        var numItems = source.readInt();
        return new StreamDbPrefetchBlock(name, firstItemIndex, numItems);
    }
}
