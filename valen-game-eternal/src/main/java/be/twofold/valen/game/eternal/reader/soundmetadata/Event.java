package be.twofold.valen.game.eternal.reader.soundmetadata;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record Event(
    int id,
    String name,
    float maxAttenuation,
    boolean is2d,
    boolean isLooping,
    int durationMs,
    int stopEventId,
    Ints mediaIds,
    Ints bankIds,
    Ints pathOffsets
) {
    public static Event read(BinarySource source) throws IOException {
        var id = source.readInt();
        var name = source.readString(StringFormat.INT_LENGTH);
        var maxAttenuation = source.readFloat();
        var is2d = source.readBool(BoolFormat.BYTE);
        var isLooping = source.readBool(BoolFormat.BYTE);
        var durationMs = source.readInt();
        var stopEventId = source.readInt();
        var mediaIds = source.readInts(source.readInt());
        var bankIds = source.readInts(source.readInt());
        var pathOffsets = source.readInts(source.readInt());

        return new Event(
            id,
            name,
            maxAttenuation,
            is2d,
            isLooping,
            durationMs,
            stopEventId,
            mediaIds,
            bankIds,
            pathOffsets
        );
    }
}
