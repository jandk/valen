package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.util.*;

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
    public static Md6AnimMapOffsets read(BetterBuffer buffer) {
        short constRRLEOffset = buffer.getShort();
        short constSRLEOffset = buffer.getShort();
        short constTRLEOffset = buffer.getShort();
        short constURLEOffset = buffer.getShort();
        short animRRLEOffset = buffer.getShort();
        short animSRLEOffset = buffer.getShort();
        short animTRLEOffset = buffer.getShort();
        short animURLEOffset = buffer.getShort();

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
