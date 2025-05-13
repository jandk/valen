package be.twofold.valen.game.darkages.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbPrefetchHeader(
    int numPrefetchBlocks,
    int totalLength
) {
    public static StreamDbPrefetchHeader read(DataSource source) throws IOException {
        var numPrefetchBlocks = source.readInt();
        var totalLength = source.readInt();
        return new StreamDbPrefetchHeader(numPrefetchBlocks, totalLength);
    }
}
