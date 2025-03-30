package be.twofold.valen.export.png;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.format.png.*;

import java.io.*;

public final class PngExporter implements TextureExporter {
    @Override
    public String getID() {
        return "texture.png";
    }

    @Override
    public String getName() {
        return "PNG (Portable Network Graphics)";
    }

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
                 R16G16B16_SFLOAT,
                 R16G16B16A16_SFLOAT,
                 BC6H_UFLOAT,
                 BC6H_SFLOAT -> null;
        };
    }

    @Override
    public void export(Texture texture, OutputStream out) throws IOException {
        var chosenFormat = chooseFormat(texture.format());
        var decoded = texture.firstOnly().convert(chosenFormat);
        var stripped = stripAlpha(decoded);
        var format = mapPngFormat(stripped);

        // TODO: How to handle closing the output stream?
        new PngOutputStream(out, format).writeImage(stripped.surfaces().getFirst().data());
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    private static Texture stripAlpha(Texture texture) {
        if (texture.format() == TextureFormat.R8G8B8A8_UNORM) {
            // Try to strip alpha
            var surface = texture.surfaces().getFirst();
            var data = surface.data();
            for (var i = 3; i < data.length; i += 4) {
                if (data[i] != (byte) 0xFF) {
                    return texture;
                }
            }

            // Found no alpha, so strip it
            var newArray = new byte[data.length / 4 * 3];
            for (int i = 0, o = 0; i < data.length; i += 4, o += 3) {
                newArray[o + 0] = data[i + 0];
                newArray[o + 1] = data[i + 1];
                newArray[o + 2] = data[i + 2];
            }

            return Texture.fromSurface(surface.withData(newArray), TextureFormat.R8G8B8_UNORM);
        }
        return texture;
    }

    private PngFormat mapPngFormat(Texture texture) {
        var w = texture.width();
        var h = texture.height();
        return switch (texture.format()) {
            case R8_UNORM -> new PngFormat(w, h, PngColorType.Gray, 8, false);
            case R8G8B8_UNORM -> new PngFormat(w, h, PngColorType.Rgb, 8, false);
            case R8G8B8A8_UNORM -> new PngFormat(w, h, PngColorType.RgbAlpha, 8, false);
            case R16_UNORM -> new PngFormat(w, h, PngColorType.Gray, 16, false);
            case R16G16B16A16_UNORM -> new PngFormat(w, h, PngColorType.RgbAlpha, 16, false);
            default -> throw new UnsupportedOperationException("Unsupported format: " + texture.format());
        };
    }
}
