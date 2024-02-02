package be.twofold.valen.reader.file;

import be.twofold.valen.core.util.*;

import java.time.*;

public record File(
    Instant creationTime,
    byte[] data
) {
    public static File read(BetterBuffer buffer) {
        Instant creationTime = Instant.ofEpochSecond(buffer.getInt());
        byte[] data = buffer.getBytes(buffer.length() - 4);

        return new File(creationTime, data);
    }
}
