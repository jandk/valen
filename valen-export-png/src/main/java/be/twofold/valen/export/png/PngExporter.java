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
            // sRGB sources preserve sRGB encoding in output
            case R8_SRGB -> TextureFormat.R8_SRGB;
            case R8G8B8_SRGB, B8G8R8_SRGB -> TextureFormat.R8G8B8_SRGB;
            case R8G8B8A8_SRGB, B8G8R8A8_SRGB,
                 BC1_SRGB, BC2_SRGB, BC3_SRGB, BC7_SRGB -> TextureFormat.R8G8B8A8_SRGB;

            // UNORM / data sources
            case R8_UNORM, BC4_UNORM, BC4_SNORM -> TextureFormat.R8_UNORM;
            case R8G8_UNORM,
                 R8G8B8_UNORM, B8G8R8_UNORM,
                 BC5_UNORM, BC5_SNORM -> TextureFormat.R8G8B8_UNORM;
            case R8G8B8A8_UNORM, B8G8R8A8_UNORM,
                 BC1_UNORM, BC2_UNORM, BC3_UNORM, BC7_UNORM -> TextureFormat.R8G8B8A8_UNORM;
            case R16_UNORM -> TextureFormat.R16_UNORM;
            case R16G16B16A16_UNORM -> TextureFormat.R16G16B16A16_UNORM;

            // Float — should ideally be exported to OpenEXR; clamped to LDR for now
            case R16_SFLOAT -> TextureFormat.R8_UNORM;
            case R16G16_SFLOAT, R16G16B16_SFLOAT,
                 BC6H_UFLOAT, BC6H_SFLOAT -> TextureFormat.R8G8B8_UNORM;
            case R16G16B16A16_SFLOAT -> TextureFormat.R8G8B8A8_UNORM;
        };
    }

    @Override
    protected void doExport(Texture texture, OutputStream out) throws IOException {
        var stripped = stripAlpha(texture);
        var format = mapPngFormat(stripped);

        var data = stripped.surfaces().getFirst().data().toArray();
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

    private static Texture stripAlpha(Texture texture) {
        var rgbFormat = switch (texture.format()) {
            case R8G8B8A8_UNORM -> TextureFormat.R8G8B8_UNORM;
            case R8G8B8A8_SRGB -> TextureFormat.R8G8B8_SRGB;
            default -> null;
        };
        if (rgbFormat == null) {
            return texture;
        }

        // Try to strip alpha
        var surface = texture.surfaces().getFirst();
        var raw = surface.data().toArray();
        for (var i = 0; i < raw.length; i += 4) {
            if (raw[i + 3] != (byte) 0xFF) {
                return texture;
            }
        }

        // Found no alpha, so strip it
        var stripped = new byte[raw.length / 4 * 3];
        for (int i = 0, o = 0; i < raw.length; i += 4, o += 3) {
            stripped[o] = raw[i];
            stripped[o + 1] = raw[i + 1];
            stripped[o + 2] = raw[i + 2];
        }

        return Texture.fromSurface(surface.withData(stripped).withFormat(rgbFormat), rgbFormat);
    }

    private PngFormat mapPngFormat(Texture texture) {
        var w = texture.width();
        var h = texture.height();
        return switch (texture.format()) {
            case R8_UNORM, R8_SRGB -> new PngFormat(w, h, PngColorType.Gray, 8, false);
            case R8G8B8_UNORM, R8G8B8_SRGB -> new PngFormat(w, h, PngColorType.Rgb, 8, false);
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB -> new PngFormat(w, h, PngColorType.RgbAlpha, 8, false);
            case R16_UNORM -> new PngFormat(w, h, PngColorType.Gray, 16, false);
            case R16G16B16A16_UNORM -> new PngFormat(w, h, PngColorType.RgbAlpha, 16, false);
            default -> throw new UnsupportedOperationException("Unsupported format: " + texture.format());
        };
    }
}
