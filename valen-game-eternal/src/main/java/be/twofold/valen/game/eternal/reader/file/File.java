package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.util.collect.*;

import java.io.*;
import java.time.*;

public record File(
    Instant creationTime,
    Bytes data
) {
    public static File read(BinarySource source) throws IOException {
        var creationTime = Instant.ofEpochSecond(source.readInt());
        var data = source.readBytes(Math.toIntExact(source.size() - 4));

        return new File(creationTime, data);
    }
}
