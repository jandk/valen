package be.twofold.valen.reader.md6anim;

import be.twofold.valen.*;

public record Md6AnimMapOffsets(
    //offset to RLE stream that specifies how many constant R joints there are and which target joint indices they map to
    short constRRLEOffset,
    //offset to RLE stream that specifies how many constant S joints there are and which target joint indices they map to
    short constSRLEOffset,
    //offset to RLE stream that specifies how many constant T joints there are and which target joint indices they map to
    short constTRLEOffset,
    //offset to RLE stream that specifies how many constant user channels there are and which target user channel indices they map to
    short constURLEOffset,
    //offset to RLE stream that specifies how many animated R joints there are and which target joint indices they map to
    short animRRLEOffset,
    //offset to RLE stream that specifies how many animated S joints there are and which target joint indices they map to
    short animSRLEOffset,
    //offset to RLE stream that specifies how many animated T joints there are and which target joint indices they map to
    short animTRLEOffset,
    //offset to RLE stream that specifies how many animated user channels there are and which target user channel indices they map to
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
