package be.twofold.valen.game.doom.readers.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.time.*;

public record ImageHeader(
    Instant timestamp,
    int type,
    int width,
    int height,
    int depth,
    int mipCount,
    boolean environment,
    int format,
    int filter,
    int repeat
) {
    public static ImageHeader read(BinaryReader reader) throws IOException {
        var timestamp = Instant.ofEpochSecond(reader.readInt());
        reader.expectInt(0x074D_4942);
        var type = reader.readInt();
        var width = reader.readInt();
        var height = reader.readInt();
        var depth = reader.readInt();
        var mipCount = reader.readInt();
        var environment = reader.readBool(BoolFormat.BYTE);
        var format = reader.readInt();
        var filter = reader.readInt();
        var repeat = reader.readInt();
        reader.expectZeros(1);

        return new ImageHeader(
            timestamp,
            type,
            width,
            height,
            depth,
            mipCount,
            environment,
            format,
            filter,
            repeat
        );
    }
}
