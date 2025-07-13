package be.twofold.valen.game.darkages.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbPrefetchBlock(
    long name,
    int firstItemIndex,
    int numItems
) {
    public static StreamDbPrefetchBlock read(BinaryReader reader) throws IOException {
        var name = reader.readLong();
        var firstItemIndex = reader.readInt();
        var numItems = reader.readInt();
        return new StreamDbPrefetchBlock(name, firstItemIndex, numItems);
    }
}
