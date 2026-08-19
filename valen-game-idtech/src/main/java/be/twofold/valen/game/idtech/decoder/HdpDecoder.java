package be.twofold.valen.game.idtech.decoder;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.game.idtech.decoder.hdp.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public final class HdpDecoder implements PageDecoder {
    private final boolean hasAlphaChannel;
    private final boolean srgb;

    public HdpDecoder(boolean hasAlphaChannel, boolean srgb) {
        this.hasAlphaChannel = hasAlphaChannel;
        this.srgb = srgb;
    }

    @Override
    public Surface decode(Bytes bytes, int width, int height) throws IOException {
        try (var source = BitSource.big(BinarySource.wrap(bytes))) {
            var data = HdpDec.decode(source, width, height, hasAlphaChannel);
            var format = srgb ? TextureFormat.R8G8B8A8_SRGB : TextureFormat.R8G8B8A8_UNORM;
            return new Surface(format, width, height, 1, Bytes.wrap(data));
        }
    }
}
