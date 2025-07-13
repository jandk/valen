package be.twofold.valen.game.darkages.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbPrefetchHeader(
    int numPrefetchBlocks,
    int totalLength
) {
    public static StreamDbPrefetchHeader read(BinaryReader reader) throws IOException {
        var numPrefetchBlocks = reader.readInt();
        var totalLength = reader.readInt();
        return new StreamDbPrefetchHeader(numPrefetchBlocks, totalLength);
    }
}
