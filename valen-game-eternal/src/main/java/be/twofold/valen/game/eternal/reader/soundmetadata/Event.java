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
    public static Event read(BinaryReader reader) throws IOException {
        var id = reader.readInt();
        var name = reader.readString(StringFormat.INT_LENGTH);
        var maxAttenuation = reader.readFloat();
        var is2d = reader.readBool(BoolFormat.BYTE);
        var isLooping = reader.readBool(BoolFormat.BYTE);
        var durationMs = reader.readInt();
        var stopEventId = reader.readInt();
        var mediaIds = reader.readInts(reader.readInt());
        var bankIds = reader.readInts(reader.readInt());
        var pathOffsets = reader.readInts(reader.readInt());

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
