package be.twofold.valen.writer.png;

import be.twofold.valen.converter.decoder.*;
import be.twofold.valen.core.texture.*;

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
        var data = decoder.decode(texture.surfaces().getFirst());

        try (var output = new PngOutputStream(Channels.newOutputStream(channel), format)) {
            output.writeImage(data);
        }
    }

    private BCDecoder mapDecoder(TextureFormat format) {
        return switch (format) {
            case Bc1Unorm, Bc1UnormSrgb, Bc1Typeless -> new BC1Decoder(3, 0, 1, 2);
            case Bc3Unorm, Bc3UnormSrgb, Bc3Typeless -> new BC3Decoder(4, 0, 1, 2, 3);
            case Bc4Unorm, Bc4Typeless -> new BC4UDecoder(1, 0);
            case Bc5Unorm, Bc5Typeless -> new BC5UDecoder(3, 0, 1, 2, normalizeNormalMap);
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }

    private PngFormat mapPngFormat(Texture texture) {
        var width = texture.width();
        var height = texture.height();
        return switch (texture.format()) {
            case Bc1Unorm, Bc1Typeless, Bc5Unorm, Bc5Typeless ->
                new PngFormat(width, height, PngColorType.Rgb, 8, true);
            case Bc1UnormSrgb -> new PngFormat(width, height, PngColorType.Rgb, 8, false);
            case Bc3Unorm, Bc3Typeless -> new PngFormat(width, height, PngColorType.RgbAlpha, 8, true);
            case Bc3UnormSrgb -> new PngFormat(width, height, PngColorType.RgbAlpha, 8, false);
            case Bc4Unorm, Bc4Typeless -> new PngFormat(width, height, PngColorType.Gray, 8, true);
            default -> throw new UnsupportedOperationException("Unsupported format: " + texture.format());
        };
    }
}
