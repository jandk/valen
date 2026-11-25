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
    int format,
    int unk8,
    int unk9
) {
    public static ImageHeader read(BinaryReader reader) throws IOException {
        var timestamp = Instant.ofEpochSecond(reader.readInt());
        reader.expectInt(0x074D_4942);
        var type = reader.readInt();
        var width = reader.readInt();
        var height = reader.readInt();
        var depth = reader.readInt();
        var mipCount = reader.readInt();
        reader.expectByte((byte) 0);
        var format = reader.readInt();
        var unk8 = reader.readInt();
        var unk9 = reader.readInt();
        reader.expectByte((byte) 0);

        return new ImageHeader(
            timestamp,
            type,
            width,
            height,
            depth,
            mipCount,
            format,
            unk8,
            unk9
        );
    }
}
