package be.twofold.valen.game.eternal.reader.file;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.time.*;

public record File(
    Instant creationTime,
    Bytes data
) {
    public static File read(BinaryReader reader) throws IOException {
        var creationTime = Instant.ofEpochSecond(reader.readInt());
        var data = reader.readRemaining();

        return new File(creationTime, data);
    }
}
