package be.twofold.valen.game.eternal.reader.md6anim;

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
    public static Md6AnimMapOffsets read(BinaryReader reader) throws IOException {
        short constRRLEOffset = reader.readShort();
        short constSRLEOffset = reader.readShort();
        short constTRLEOffset = reader.readShort();
        short constURLEOffset = reader.readShort();
        short animRRLEOffset = reader.readShort();
        short animSRLEOffset = reader.readShort();
        short animTRLEOffset = reader.readShort();
        short animURLEOffset = reader.readShort();

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
