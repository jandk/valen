package be.twofold.valen.game.eternal.reader.file;

import be.twofold.valen.core.io.*;

import java.io.*;
import java.nio.*;
import java.time.*;

public record File(
    Instant creationTime,
    ByteBuffer data
) {
    public static File read(BinaryReader reader) throws IOException {
        Instant creationTime = Instant.ofEpochSecond(reader.readInt());
        ByteBuffer data = reader.readBuffer(Math.toIntExact(reader.size() - 4));

        return new File(creationTime, data);
    }
}
