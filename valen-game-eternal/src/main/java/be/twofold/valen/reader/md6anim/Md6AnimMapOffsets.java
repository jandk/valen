package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.io.*;

import java.io.*;

public record Md6AnimMapOffsets(
    short constRRLEOffset,
    short constSRLEOffset,
    short constTRLEOffset,
    short constURLEOffset,
    short animRRLEOffset,
    short animSRLEOffset,
    short animTRLEOffset,
    short animURLEOffset
) {
    public static Md6AnimMapOffsets read(DataSource source) throws IOException {
        short constRRLEOffset = source.readShort();
        short constSRLEOffset = source.readShort();
        short constTRLEOffset = source.readShort();
        short constURLEOffset = source.readShort();
        short animRRLEOffset = source.readShort();
        short animSRLEOffset = source.readShort();
        short animTRLEOffset = source.readShort();
        short animURLEOffset = source.readShort();

        return new Md6AnimMapOffsets(
            constRRLEOffset,
            constSRLEOffset,
            constTRLEOffset,
            constURLEOffset,
            animRRLEOffset,
            animSRLEOffset,
            animTRLEOffset,
            animURLEOffset
        );
    }
}
