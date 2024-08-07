package be.twofold.valen.export.png;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.*;

import java.io.*;

public final class PngExporter implements Exporter<Texture> {
    // TODO: Make this configurable
    private final boolean normalizeNormalMap = false;

    @Override
    public String getExtension() {
        return "png";
    }

    @Override
    public Class<Texture> getSupportedType() {
        return Texture.class;
    }

    @Override
    public void export(Texture texture, OutputStream out) throws IOException {
        var format = mapPngFormat(texture);
        var decoder = mapDecoder(texture.format());

        byte[] data;
        if (decoder != null) {
            data = decoder.decode(texture.width(), texture.height(), texture.surfaces().getFirst().data(), 0);
        } else {
            data = texture.surfaces().getFirst().data();
        }

        // TODO: How to handle closing the output stream?
        new PngOutputStream(out, format).writeImage(data);
    }

    private BlockDecoder mapDecoder(TextureFormat format) {
        return switch (format) {
            case Bc1UNorm, Bc1UNormSrgb -> BlockDecoder.create(BlockFormat.BC1, PixelOrder.RGBA);
            case Bc3UNorm, Bc3UNormSrgb -> BlockDecoder.create(BlockFormat.BC3, PixelOrder.RGBA);
            case Bc4UNorm -> BlockDecoder.create(BlockFormat.BC4Unsigned, PixelOrder.R);
            case Bc5UNorm -> BlockDecoder.create(normalizeNormalMap ? BlockFormat.BC5UnsignedNormalized : BlockFormat.BC5Unsigned, PixelOrder.RGB);
            case Bc7UNorm, Bc7UNormSrgb -> BlockDecoder.create(BlockFormat.BC7, PixelOrder.RGBA);
            case R8G8B8A8UNorm, A8UNorm -> null;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }

    private PngFormat mapPngFormat(Texture texture) {
        var width = texture.width();
        var height = texture.height();
        return switch (texture.format()) {
            case Bc1UNorm, Bc3UNorm, Bc7UNorm, R8G8B8A8UNorm -> new PngFormat(width, height, PngColorType.RgbAlpha, 8, true);
            case Bc1UNormSrgb, Bc3UNormSrgb, Bc7UNormSrgb -> new PngFormat(width, height, PngColorType.RgbAlpha, 8, false);
            case Bc4UNorm, A8UNorm -> new PngFormat(width, height, PngColorType.Gray, 8, true);
            case Bc5UNorm -> new PngFormat(width, height, PngColorType.Rgb, 8, true);
            default -> throw new UnsupportedOperationException("Unsupported format: " + texture.format());
        };
    }
}
