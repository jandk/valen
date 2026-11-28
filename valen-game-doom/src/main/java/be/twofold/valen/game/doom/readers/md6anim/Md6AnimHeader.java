package be.twofold.valen.game.doom.readers.md6anim;

import be.twofold.valen.game.doom.readers.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;
import java.time.*;

public record Md6AnimHeader(
    Instant timestamp,
    Instant skeletonTimestamp,
    String skelName,
    Bounds translatedBounds,
    Bounds normalizedBounds,
    int size
) {
    private static final int MAGIC = 0x2602_414D;

    public static Md6AnimHeader read(BinaryReader reader) throws IOException {
        reader.expectInt(MAGIC);
        var timestamp = Instant.ofEpochSecond(reader.readInt());
        var skeletonTimestamp = Instant.ofEpochSecond(reader.readInt());
        var skelName = IdStrings.read(reader);
        var translatedBounds = readShortBounds(reader);
        var normalizedBounds = readShortBounds(reader);
        var size = reader.readInt();

        return new Md6AnimHeader(
            timestamp,
            skeletonTimestamp,
            skelName,
            translatedBounds,
            normalizedBounds,
            size
        );
    }

    private static Bounds readShortBounds(BinaryReader reader) throws IOException {
        return new Bounds(
            reader.readShort(), reader.readShort(), reader.readShort(),
            reader.readShort(), reader.readShort(), reader.readShort()
        );
    }
}
