package be.twofold.valen.reader.file;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.time.*;

public record File(
    Instant creationTime,
    byte[] data
) {
    public static File read(DataSource source) throws IOException {
        Instant creationTime = Instant.ofEpochSecond(source.readInt());
        byte[] data = source.readBytes(Math.toIntExact(source.size() - 4));

        return new File(creationTime, data);
    }
}
