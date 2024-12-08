package be.twofold.valen.game.greatcircle.reader.streamdb;

import be.twofold.valen.core.io.*;

import java.io.*;

public record StreamDbEntry(
    int offset16,
    int length,
    int compressionMode
) {
    public static StreamDbEntry read(DataSource source) throws IOException {
        int offset16 = source.readInt();
        int length = source.readInt();
        int compressionMode = source.readInt();

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
