package be.twofold.valen.export.png;

import be.twofold.tinybcdec.BlockFormat;
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
        return switch (format.blockFormat()) {
            case BC1 -> BlockDecoder.create(BlockFormat.BC1, PixelOrder.RGBA);
            case BC3 -> BlockDecoder.create(BlockFormat.BC3, PixelOrder.RGBA);
            case BC4 -> BlockDecoder.create(BlockFormat.BC4Unsigned, PixelOrder.R);
            case BC5 -> BlockDecoder.create(normalizeNormalMap ? BlockFormat.BC5UnsignedNormalized : BlockFormat.BC5Unsigned, PixelOrder.RGB);
            case BC7 -> BlockDecoder.create(BlockFormat.BC7, PixelOrder.RGBA);
            // case R8G8B8A8UNorm, A8UNorm -> null;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }

    private PngFormat mapPngFormat(Texture texture) {
        var colorType = switch (texture.format().blockFormat()) {
            case BC1, BC3, BC7 -> PngColorType.RgbAlpha;
            case BC4 -> PngColorType.Gray;
            case BC5 -> PngColorType.Rgb;
            default -> throw new UnsupportedOperationException("Unsupported format: " + texture.format());
        };
        boolean linear = texture.format().numericFormat() == NumericFormat.UNorm;
        return new PngFormat(texture.width(), texture.height(), colorType, 8, linear);
    }
}
