package be.twofold.valen.game.greatcircle.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StreamDbEntry(
    int offset16,
    int length,
    StreamDbCompression compressionType
) {
    public static StreamDbEntry read(BinaryReader reader) throws IOException {
        var offset16 = reader.readInt();
        var length = reader.readInt();
        var compressionType = StreamDbCompression.read(reader);

        return new StreamDbEntry(
            offset16,
            length,
            compressionType
        );
    }
}
