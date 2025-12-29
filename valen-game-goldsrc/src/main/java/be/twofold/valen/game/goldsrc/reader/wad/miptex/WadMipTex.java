package be.twofold.valen.game.goldsrc.reader.wad.miptex;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record WadMipTex(
    String name,
    int width,
    int height,
    ArrayList<Bytes> mips,
    Bytes palette
) {
    public static WadMipTex read(BinarySource source) throws IOException {
        var position = source.position();

        var name = source.readString(16).trim();
        var width = source.readInt();
        var height = source.readInt();
        var offsets = source.readInts(4);

        var mips = new ArrayList<Bytes>(4);
        for (int i = 0; i < 4; i++) {
            int w = Math.max(width >> i, 1);
            int h = Math.max(height >> i, 1);
            source.position(position + offsets.get(i));
            mips.add(source.readBytes(w * h));
        }

        var paletteSize = source.readShort();
        var palette = source.readBytes(paletteSize * 3);

        return new WadMipTex(name, width, height, mips, palette);
    }
}
