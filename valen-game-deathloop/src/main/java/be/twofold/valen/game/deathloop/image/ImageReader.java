package be.twofold.valen.game.deathloop.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;

import java.io.*;
import java.util.*;

public final class ImageReader {
    public Texture read(DataSource source) throws IOException {
        var header = ImageHeader.read(source);
        var format = mapFormat(header.textureFormat());
        if (header.textureFormat() == ImageTextureFormat.FMT_BC6H) {
            System.out.println("Found format");
        }

        var surfaces = new ArrayList<Surface>();
        for (int i = 0; i < header.embeddedLevels(); i++) {
            var mipHeader = ImageMipHeader.read(source);
            var mipData = source.readBytes(mipHeader.dataSize());

            var surface = new Surface(mipHeader.width(), mipHeader.height(), format, mipData);
            surfaces.add(surface);
        }
        return new Texture(header.embeddedWidth(), header.embeddedHeight(), format, surfaces, false);
    }

    private TextureFormat mapFormat(ImageTextureFormat textureFormat) {
        return switch (textureFormat) {
            case FMT_R8 -> TextureFormat.R8_UNORM;
            case FMT_R16F -> TextureFormat.R16_SFLOAT;
            case FMT_R32F -> TextureFormat.R32_SFLOAT;
            case FMT_R16_UNORM -> TextureFormat.R16_UNORM;
            case FMT_RG32F -> TextureFormat.R32G32_SFLOAT;
            case FMT_RGBA -> TextureFormat.R8G8B8A8_UNORM;
            case FMT_RGBA16F -> TextureFormat.R16G16B16A16_SFLOAT;
            case FMT_RGBA32F -> TextureFormat.R32G32B32A32_SFLOAT;
            case FMT_BC1 -> TextureFormat.BC1_SRGB;
            case FMT_BC3 -> TextureFormat.BC3_SRGB;
            case FMT_BC4 -> TextureFormat.BC4_UNORM;
            case FMT_BC5S -> TextureFormat.BC5_SNORM;
            case FMT_BC6H -> TextureFormat.BC6H_UFLOAT;
            case FMT_BC7 -> TextureFormat.BC7_SRGB;
        };
    }
}
