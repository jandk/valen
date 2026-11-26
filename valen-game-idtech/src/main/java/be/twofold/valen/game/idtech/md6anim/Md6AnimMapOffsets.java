package be.twofold.valen.game.idtech.md6anim;

import wtf.reversed.toolbox.io.*;

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
    int constRRLEOffset,
    int constSRLEOffset,
    int constTRLEOffset,
    int constURLEOffset,
    int animRRLEOffset,
    int animSRLEOffset,
    int animTRLEOffset,
    int animURLEOffset
) {
    public static Md6AnimMapOffsets read(BinaryReader reader) throws IOException {
        var constRRLEOffset = reader.readUnsignedShort();
        var constSRLEOffset = reader.readUnsignedShort();
        var constTRLEOffset = reader.readUnsignedShort();
        var constURLEOffset = reader.readUnsignedShort();
        var animRRLEOffset = reader.readUnsignedShort();
        var animSRLEOffset = reader.readUnsignedShort();
        var animTRLEOffset = reader.readUnsignedShort();
        var animURLEOffset = reader.readUnsignedShort();

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
