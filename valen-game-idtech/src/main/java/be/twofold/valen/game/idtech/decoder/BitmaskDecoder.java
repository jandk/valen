package be.twofold.valen.game.idtech.decoder;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

/**
 * Expands a one bit per pixel mask, least significant bit first, to one byte per pixel.
 */
public final class BitmaskDecoder implements PageDecoder {
    private final TextureFormat format;

    public BitmaskDecoder(TextureFormat format) {
        this.format = Check.nonNull(format, "format");
    }

    @Override
    public Surface decode(Bytes bytes, int width, int height) {
        var pixels = new byte[width * height];
        Check.argument(bytes.length() >= Math.ceilDiv(pixels.length, 8), "not enough bits");

        for (var i = 0; i < pixels.length; i++) {
            var bit = bytes.get(i >> 3) >> (i & 7) & 1;
            pixels[i] = (byte) -bit;
        }
        return new Surface(format, width, height, 1, Bytes.wrap(pixels));
    }
}
