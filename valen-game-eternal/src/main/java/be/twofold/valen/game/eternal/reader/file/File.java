package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.time.*;

public record File(
    Instant creationTime,
    Bytes data
) {
    public static File read(BinaryReader reader) throws IOException {
        var creationTime = Instant.ofEpochSecond(reader.readInt());
        var data = reader.readBytes(Math.toIntExact(reader.size() - 4));

        return new File(creationTime, data);
    }
}
