package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.time.*;

public record File(
    Instant creationTime,
    ByteBuffer data
) {
    public static File read(DataSource source) throws IOException {
        Instant creationTime = Instant.ofEpochSecond(source.readInt());
        ByteBuffer data = source.readBuffer(Math.toIntExact(source.size() - 4));

        return new File(creationTime, data);
    }
}
