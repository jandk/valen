package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbEntry(
    int offset16,
    int length,
    StreamerCompression compressionMode
) {
    public static StreamDbEntry read(BinaryReader reader) throws IOException {
        int offset16 = reader.readInt();
        int length = reader.readInt();
        StreamerCompression compressionMode = StreamerCompression.fromValue(reader.readInt());

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
