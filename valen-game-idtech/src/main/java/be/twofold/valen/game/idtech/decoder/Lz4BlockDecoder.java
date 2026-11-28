package be.twofold.valen.game.idtech.decoder;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.compress.*;
import wtf.reversed.toolbox.util.*;

public final class Lz4BlockDecoder implements PageDecoder {
    private final TextureFormat format;

    public Lz4BlockDecoder(TextureFormat format) {
        this.format = Check.nonNull(format, "format");
    }

    @Override
    public Surface decode(Bytes bytes, int width, int height) {
        var size = format.surfaceSize(width, height, 1);
        var decompressed = Decompressor.lz4Block()
            .decompress(bytes, size);

        return new Surface(format, width, height, 1, decompressed);
    }
}
