package be.twofold.valen.reader.streamdb;

import java.nio.*;

public record StreamDbPrefetchHeader(
    int numPrefetchBlocks,
    int totalLength
) {
    static final int Size = 0x08;

    public static StreamDbPrefetchHeader read(ByteBuffer buffer) {
        int numPrefetchBlocks = buffer.getInt(0x00);
        int totalLength = buffer.getInt(0x04);
        return new StreamDbPrefetchHeader(numPrefetchBlocks, totalLength);
    }
}
