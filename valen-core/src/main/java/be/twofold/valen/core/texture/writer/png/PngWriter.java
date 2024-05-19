package be.twofold.valen.core.texture.writer.png;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.texture.decoder.*;

import java.io.*;
import java.nio.channels.*;

public final class PngWriter {
    private final WritableByteChannel channel;
    private final boolean normalizeNormalMap;

    public PngWriter(WritableByteChannel channel) {
        this(channel, false);
    }

    public PngWriter(WritableByteChannel channel, boolean normalizeNormalMap) {
        this.channel = channel;
        this.normalizeNormalMap = normalizeNormalMap;
    }

    public void write(Texture texture) throws IOException {
        var format = mapPngFormat(texture);
        var decoder = mapDecoder(texture.format());
        var data = decoder.decode(texture.surfaces().getFirst().data(), texture.width(), texture.height());

        try (var output = new PngOutputStream(Channels.newOutputStream(channel), format)) {
            output.writeImage(data);
        }
    }

    private BCDecoder mapDecoder(TextureFormat format) {
        return switch (format) {
            case Bc1UNorm, Bc1UNormSrgb -> new BC1Decoder();
            case Bc3UNorm, Bc3UNormSrgb -> new BC3Decoder();
            case Bc4UNorm -> new BC4UDecoder();
            case Bc5UNorm -> new BC5UDecoder(normalizeNormalMap);
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }

    private PngFormat mapPngFormat(Texture texture) {
        var width = texture.width();
        var height = texture.height();
        return switch (texture.format()) {
            case Bc1UNorm, Bc3UNorm -> new PngFormat(width, height, PngColorType.RgbAlpha, 8, true);
            case Bc1UNormSrgb, Bc3UNormSrgb -> new PngFormat(width, height, PngColorType.RgbAlpha, 8, false);
            case Bc4UNorm -> new PngFormat(width, height, PngColorType.Gray, 8, true);
            case Bc5UNorm -> new PngFormat(width, height, PngColorType.Rgb, 8, true);
            default -> throw new UnsupportedOperationException("Unsupported format: " + texture.format());
        };
    }
}
