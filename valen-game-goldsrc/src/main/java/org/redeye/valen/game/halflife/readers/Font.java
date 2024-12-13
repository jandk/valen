package org.redeye.valen.game.halflife.readers;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;

import java.io.*;
import java.util.*;

public class Font {

    public record CharInfo(short a, short b) {

        public static CharInfo read(DataSource source) throws IOException {
            return new CharInfo(source.readShort(), source.readShort());
        }
    }

    public static Texture read(DataSource source) throws IOException {
        var start = source.tell();
        var width = source.readInt();
        var height = source.readInt();
        var rowCount = source.readInt();
        var rowHeight = source.readInt();
        var charInfo = new ArrayList<CharInfo>(256);
        for (int i = 0; i < 256; i++) {
            charInfo.add(CharInfo.read(source));
        }
        width = 256;

        int pixelCount = width * height;
        var indices = source.readBytes(pixelCount);
        var flags = source.readInt();
        var palette = source.readBytes(256 * 3);
        var surfaces = new ArrayList<Surface>(1);
        surfaces.add(MipTex.buildSurface(width, height, indices, palette, true));
        return new Texture(width, height, TextureFormat.R8G8B8A8_UNORM, surfaces, false);
    }
}
