package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbEntry(
    int offset16,
    int length,
    StreamerCompression compressionMode
) {
    public static StreamDbEntry read(BinarySource source) throws IOException {
        int offset16 = source.readInt();
        int length = source.readInt();
        StreamerCompression compressionMode = StreamerCompression.fromValue(source.readInt());

        return new StreamDbEntry(
            offset16,
            length,
            compressionMode
        );
    }

    public long offset() {
        return offset16 * 16L;
    }
}
