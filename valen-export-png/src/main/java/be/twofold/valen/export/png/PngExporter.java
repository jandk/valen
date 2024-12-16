package be.twofold.valen.export.png;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.texture.*;

import java.io.*;

public final class PngExporter implements TextureExporter {
    @Override
    public String getExtension() {
        return "png";
    }

    @Override
    public Class<Texture> getSupportedType() {
        return Texture.class;
    }

    @Override
    public TextureFormat chooseFormat(TextureFormat format) {
        return switch (format) {
            case R8_UNORM,
                 BC4_UNORM,
                 BC4_SNORM -> TextureFormat.R8_UNORM;
            case R8G8_UNORM,
                 R8G8B8_UNORM,
                 B8G8R8_UNORM,
                 BC5_UNORM,
                 BC5_SNORM -> TextureFormat.R8G8B8_UNORM;
            case R8G8B8A8_UNORM, B8G8R8A8_UNORM,
                 BC1_UNORM,
                 BC1_SRGB,
                 BC2_UNORM,
                 BC2_SRGB,
                 BC3_UNORM,
                 BC3_SRGB,
                 BC7_UNORM,
                 BC7_SRGB -> TextureFormat.R8G8B8A8_UNORM;
            case R16_UNORM -> TextureFormat.R16_UNORM;
            case R16G16B16A16_UNORM -> TextureFormat.R16G16B16A16_UNORM;
            case R16_SFLOAT,
                 R16G16_SFLOAT,
                 R16G16B16A16_SFLOAT,
                 BC6H_UFLOAT,
                 BC6H_SFLOAT -> null;
        };
    }

    @Override
    public void export(Texture texture, OutputStream out) throws IOException {
        var surface = texture.surfaces().getFirst();
        var chosenFormat = chooseFormat(texture.format());
        var decoded = TextureConverter.convert(texture.firstOnly(), chosenFormat);
        var stripped = stripAlpha(decoded.surfaces().getFirst());
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

    private PngFormat mapPngFormat(Surface surface, TextureFormat format) {
        var w = surface.width();
        var h = surface.height();
        return switch (format) {
            case R8_UNORM -> new PngFormat(w, h, PngColorType.Gray, 8, false);
            case R8G8B8_UNORM -> new PngFormat(w, h, PngColorType.Rgb, 8, false);
            case R8G8B8A8_UNORM -> new PngFormat(w, h, PngColorType.RgbAlpha, 8, false);
            case R16_UNORM -> new PngFormat(w, h, PngColorType.Gray, 16, false);
            case R16G16B16A16_UNORM -> new PngFormat(w, h, PngColorType.RgbAlpha, 16, false);
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
