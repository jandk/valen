package be.twofold.valen.game.goldsrc.reader.wad;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.collect.*;

public final class WadUtil {
    private WadUtil() {
    }

    public static Surface buildSurface(int width, int height, Bytes indices, Bytes palette, boolean hasAlpha) {
        var bufferSize = TextureFormat.R8G8B8A8_UNORM.surfaceSize(width, height);
        var buffer = new byte[bufferSize];
        for (int i = 0; i < indices.length(); i++) {
            int colorId = indices.getUnsigned(i);
            if (hasAlpha && colorId == 255) {
                buffer[i * 4/**/] = 0;
                buffer[i * 4 + 1] = 0;
                buffer[i * 4 + 2] = 0;
                buffer[i * 4 + 3] = 0;
            } else {
                buffer[i * 4/**/] = palette.get(colorId * 3/**/);
                buffer[i * 4 + 1] = palette.get(colorId * 3 + 1);
                buffer[i * 4 + 2] = palette.get(colorId * 3 + 2);
                buffer[i * 4 + 3] = (byte) 255;
            }
        }

        return new Surface(width, height, TextureFormat.R8G8B8A8_UNORM, buffer);
    }
}
