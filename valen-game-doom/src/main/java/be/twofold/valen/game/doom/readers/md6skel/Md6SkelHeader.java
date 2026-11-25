package be.twofold.valen.game.doom.readers.md6skel;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.time.*;

public record Md6SkelHeader(
    Instant timestamp,
    int dataSize
) {
    public static final int BYTES = 12;

    public static Md6SkelHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(0x0E02_534D);
        var timestamp = Instant.ofEpochSecond(reader.readInt());
        var dataSize = reader.readInt();

        return new Md6SkelHeader(
            timestamp,
            dataSize
        );
    }
}
