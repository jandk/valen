package be.twofold.valen.game.greatcircle.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StreamDbEntry(
    int offset16,
    int length,
    StreamDbCompression compressionType
) {
    public static StreamDbEntry read(BinarySource source) throws IOException {
        var offset16 = source.readInt();
        var length = source.readInt();
        var compressionType = StreamDbCompression.read(source);

        return new StreamDbEntry(
            offset16,
            length,
            compressionType
        );
    }
}
