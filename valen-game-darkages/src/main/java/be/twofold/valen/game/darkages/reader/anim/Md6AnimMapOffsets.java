package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;

import java.io.*;

/**
 * @param constRRLEOffset offset to RLE stream that specifies how many constant R joints there are and which target joint indices they map to
 * @param constSRLEOffset offset to RLE stream that specifies how many constant S joints there are and which target joint indices they map to
 * @param constTRLEOffset offset to RLE stream that specifies how many constant T joints there are and which target joint indices they map to
 * @param constURLEOffset offset to RLE stream that specifies how many constant user channels there are and which target user channel indices they map to
 * @param animRRLEOffset  offset to RLE stream that specifies how many animated R joints there are and which target joint indices they map to
 * @param animSRLEOffset  offset to RLE stream that specifies how many animated S joints there are and which target joint indices they map to
 * @param animTRLEOffset  offset to RLE stream that specifies how many animated T joints there are and which target joint indices they map to
 * @param animURLEOffset  offset to RLE stream that specifies how many animated user channels there are and which target user channel indices they map to
 */
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
        var constRRLEOffset = reader.readShort();
        var constSRLEOffset = reader.readShort();
        var constTRLEOffset = reader.readShort();
        var constURLEOffset = reader.readShort();
        var animRRLEOffset = reader.readShort();
        var animSRLEOffset = reader.readShort();
        var animTRLEOffset = reader.readShort();
        var animURLEOffset = reader.readShort();

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
