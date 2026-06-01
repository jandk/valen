package be.twofold.valen.game.doom.readers.image;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
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
    public static ImageHeader read(BinarySource source) throws IOException {
        source.order(ByteOrder.BIG_ENDIAN);

        var timestamp = Instant.ofEpochSecond(source.readInt());
        source.expectInt(0x074D_4942);
        var type = source.readInt();
        var width = source.readInt();
        var height = source.readInt();
        var depth = source.readInt();
        var mipCount = source.readInt();
        source.expectByte((byte) 0);
        var format = source.readInt();
        var unk8 = source.readInt();
        var unk9 = source.readInt();
        source.expectByte((byte) 0);

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
