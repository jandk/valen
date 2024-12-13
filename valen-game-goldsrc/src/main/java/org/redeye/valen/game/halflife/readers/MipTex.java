package org.redeye.valen.game.halflife.readers;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;

import java.io.*;
import java.util.*;

public class MipTex {

    public static Texture read(DataSource source) throws IOException {
        var start = source.tell();
        var name = source.readString(16).trim();
        var width = source.readInt();
        var height = source.readInt();
        var offsets = new int[4];
        for (int i = 0; i < 4; i++) {
            offsets[i] = source.readInt();
        }

        int pixelCount = width * height;
        source.seek(start + offsets[3] + (pixelCount >> (3 * 2)));
        source.skip(2);
        var hasAlpha = name.startsWith("{");
        var palette = source.readBytes(256 * 3);
        var surfaces = new ArrayList<Surface>(3);
        for (int mip = 0; mip < 3; mip++) {
            source.seek(start + offsets[mip]);
            var textureSize = pixelCount >> (mip * 2);
            var indices = source.readBytes(textureSize);
            source.skip(2);
            surfaces.add(MipTex.buildSurface(width >> mip, height >> mip, indices, palette, hasAlpha));
        }
        return new Texture(width, height, TextureFormat.R8G8B8A8_UNORM, surfaces, false);
    }

    static Surface buildSurface(int width, int height, byte[] indices, byte[] palette, boolean hasAlpha) {
        var buffer = new byte[width * height * 4];
        for (int i = 0; i < indices.length; i++) {
            int colorId = Byte.toUnsignedInt(indices[i]);
            if (colorId == 255 && hasAlpha) {
                buffer[i * 4 + 0] = 0;
                buffer[i * 4 + 1] = 0;
                buffer[i * 4 + 2] = 0;
                buffer[i * 4 + 3] = 0;
                continue;
            }
            buffer[i * 4 + 0] = palette[colorId * 3 + 0];
            buffer[i * 4 + 1] = palette[colorId * 3 + 1];
            buffer[i * 4 + 2] = palette[colorId * 3 + 2];
            buffer[i * 4 + 3] = (byte) 255;
        }

        return new Surface(width, height, TextureFormat.R8G8B8A8_UNORM, buffer);
    }

}
