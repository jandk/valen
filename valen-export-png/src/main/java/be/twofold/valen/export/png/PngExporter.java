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
        var stripped = stripAlpha(decoded);
        var format = mapPngFormat(surface, stripped.format());

        // TODO: How to handle closing the output stream?
        new PngOutputStream(out, format).writeImage(stripped.data());
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private static Surface stripAlpha(Surface surface) {
        if (surface.format() == TextureFormat.R8G8B8A8_UNORM) {
            // Try to strip alpha
            byte[] array = surface.data();
            for (int i = 3; i < array.length; i += 4) {
                if (array[i] != (byte) 0xFF) {
                    return surface;
                }
            }

            // Found no alpha, so strip it
            byte[] newArray = new byte[array.length / 4 * 3];
            for (int i = 0, o = 0; i < array.length; i += 4, o += 3) {
                newArray[o + 0] = array[i + 0];
                newArray[o + 1] = array[i + 1];
                newArray[o + 2] = array[i + 2];
            }
            return new Surface(surface.width(), surface.height(), TextureFormat.R8G8B8_UNORM, newArray);
        }
        return surface;
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
