package be.twofold.valen.export.png;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.format.png.*;

import java.io.*;

public final class PngExporter extends TextureExporter {
    public PngExporter() {
        super(false);
    }

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
    protected TextureFormat chooseFormat(TextureFormat format) {
        return switch (format) {
            case R8_UNORM,
                 BC4_UNORM,
                 BC4_SNORM -> TextureFormat.R8_UNORM;
            case R8_SRGB -> TextureFormat.R8_SRGB;
            case R8G8_UNORM,
                 R8G8B8_UNORM,
                 B8G8R8_UNORM,
                 BC5_UNORM,
                 BC5_SNORM -> TextureFormat.R8G8B8_UNORM;
            case R8G8B8_SRGB,
                 B8G8R8_SRGB -> TextureFormat.R8G8B8_SRGB;
            case R8G8B8A8_UNORM,
                 B8G8R8A8_UNORM,
                 BC1_UNORM,
                 BC2_UNORM,
                 BC3_UNORM,
                 BC7_UNORM -> TextureFormat.R8G8B8A8_UNORM;
            case R8G8B8A8_SRGB,
                 B8G8R8A8_SRGB,
                 BC1_SRGB,
                 BC2_SRGB,
                 BC3_SRGB,
                 BC7_SRGB -> TextureFormat.R8G8B8A8_SRGB;
            case R16_UNORM -> TextureFormat.R16_UNORM;
            case R10G10B10A2_UNORM,
                 R16G16B16A16_UNORM -> TextureFormat.R16G16B16A16_UNORM;

            // HDR, these should be exported to EXR
            case R16_SFLOAT -> TextureFormat.R8_UNORM;
            case R11G11B10_SFLOAT,
                 R16G16_SFLOAT,
                 R16G16B16_SFLOAT,
                 BC6H_UFLOAT,
                 BC6H_SFLOAT -> TextureFormat.R8G8B8_UNORM;
            case R16G16B16A16_SFLOAT -> TextureFormat.R8G8B8A8_UNORM;
        };
    }

    @Override
    protected void doExport(Texture texture, OutputStream out) throws IOException {
        var format = mapPngFormat(texture);

        var data = texture.surfaces().getFirst().data().toArray();
        if (format.bitDepth() == 16) {
            for (int i = 0; i < data.length; i += 2) {
                var tmp = data[i];
                data[i] = data[i + 1];
                data[i + 1] = tmp;
            }
        }

        // TODO: How to handle closing the output stream?
        new PngOutputStream(out, format).writeImage(data);
    }

    private PngFormat mapPngFormat(Texture texture) {
        var w = texture.width();
        var h = texture.height();
        var linear = texture.format().toString().endsWith("_UNORM");
        return switch (texture.format()) {
            case R8_UNORM, R8_SRGB -> new PngFormat(w, h, PngColorType.Gray, 8, linear);
            case R8G8B8_UNORM, R8G8B8_SRGB -> new PngFormat(w, h, PngColorType.Rgb, 8, linear);
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB -> new PngFormat(w, h, PngColorType.RgbAlpha, 8, linear);
            case R16_UNORM -> new PngFormat(w, h, PngColorType.Gray, 16, linear);
            case R16G16B16A16_UNORM -> new PngFormat(w, h, PngColorType.RgbAlpha, 16, linear);
            default -> throw new UnsupportedOperationException("Unsupported format: " + texture.format());
        };
    }
}
