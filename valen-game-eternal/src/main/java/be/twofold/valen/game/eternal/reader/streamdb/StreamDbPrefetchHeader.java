package be.twofold.valen.game.eternal.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbPrefetchHeader(
    int numPrefetchBlocks,
    int totalLength
) {
    public static StreamDbPrefetchHeader read(BinarySource source) throws IOException {
        var numPrefetchBlocks = source.readInt();
        var totalLength = source.readInt();
        return new StreamDbPrefetchHeader(numPrefetchBlocks, totalLength);
    }
}
