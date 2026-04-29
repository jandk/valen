package be.twofold.valen.game.eternal.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StreamDbPrefetchBlock(
    long name,
    int firstItemIndex,
    int numItems
) {
    public static StreamDbPrefetchBlock read(BinarySource source) throws IOException {
        var name = source.readLong();
        var firstItemIndex = source.readInt();
        var numItems = source.readInt();

        return new StreamDbPrefetchBlock(
            name,
            firstItemIndex,
            numItems
        );
    }
}
