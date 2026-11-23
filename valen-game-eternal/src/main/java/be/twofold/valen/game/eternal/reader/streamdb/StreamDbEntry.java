package be.twofold.valen.game.eternal.reader.streamdb;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record StreamDbEntry(
    long identity,
    int offset16,
    int length
) {
    public static StreamDbEntry read(BinaryReader reader) throws IOException {
        var identity = reader.readLong();
        var offset16 = reader.readInt();
        var length = reader.readInt();

        return new StreamDbEntry(
            identity,
            offset16,
            length
        );
    }
}
