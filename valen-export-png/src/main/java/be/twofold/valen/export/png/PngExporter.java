package be.twofold.valen.export.png;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.export.*;

import java.io.*;

public final class PngExporter implements Exporter<Texture> {

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
        var surface = texture.surfaces().getFirst();
        var chosenFormat = chooseFormat(texture.format());
        var decoded = SurfaceConverter.convert(surface, chosenFormat);
        var format = mapPngFormat(surface, chosenFormat);

        // TODO: How to handle closing the output stream?
        new PngOutputStream(out, format).writeImage(decoded.data());
    }

    private TextureFormat chooseFormat(TextureFormat format) {
        if (format.isCompressed()) {
            return switch (format.block()) {
                case BC1, BC2, BC3, BC7 -> TextureFormat.R8G8B8A8_UNORM;
                case BC4 -> TextureFormat.R8_UNORM;
                case BC5 -> TextureFormat.R8G8B8_UNORM;
                default -> throw new UnsupportedOperationException("Unsupported format: " + format.block());
            };
        }

        return switch (format.order().orElseThrow()) {
            case R -> TextureFormat.R8_UNORM;
            case RGB -> TextureFormat.R8G8B8_UNORM;
            case RGBA -> TextureFormat.R8G8B8A8_UNORM;
            default -> throw new UnsupportedOperationException("Unsupported order: " + format.order());
        };
    }

    private PngFormat mapPngFormat(Surface surface, TextureFormat format) {
        var colorType = switch (format) {
            case R8_UNORM -> PngColorType.Gray;
            case R8G8B8_UNORM -> PngColorType.Rgb;
            case R8G8B8A8_UNORM -> PngColorType.RgbAlpha;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
        boolean linear = surface.format().interp() == TextureFormat.Interp.UNorm;
        return new PngFormat(surface.width(), surface.height(), colorType, 8, linear);
    }

}
