package be.twofold.valen.game.eternal.reader.image;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import be.twofold.valen.game.eternal.defines.*;
import be.twofold.valen.game.eternal.defines.TextureFormat;
import be.twofold.valen.game.idtech.defines.*;

import java.util.*;

public final class ImageMapper {
    public Texture map(Image image) {
        int minMip = image.minMip();
        int width = minMip < 0 ? image.header().pixelWidth() : image.mipInfos().get(minMip).mipPixelWidth();
        int height = minMip < 0 ? image.header().pixelHeight() : image.mipInfos().get(minMip).mipPixelHeight();
        be.twofold.valen.core.texture.TextureFormat format = toImageFormat(image.header().textureFormat());
        List<Surface> surfaces = convertMipMaps(image);
        boolean isCubeMap = image.header().textureType() == TextureType.TT_CUBIC;
        float scale = image.header().albedoSpecularScale();
        float bias = image.header().albedoSpecularBias();

        return new Texture(width, height, format, isCubeMap, surfaces, scale, bias);
    }

    private List<Surface> convertMipMaps(Image image) {
        int faces = image.header().textureType() == TextureType.TT_CUBIC ? 6 : 1;
        int mipCount = image.mipInfos().size() / faces;
        int minMip = image.minMip() < 0 ? mipCount : image.minMip();

        List<Surface> surfaces = new ArrayList<>();
        for (int face = 0; face < faces; face++) {
            for (int mip = minMip; mip < mipCount; mip++) {
                int mipIndex = mip * faces + face;
                surfaces.add(new Surface(
                    image.mipInfos().get(mipIndex).mipPixelWidth(),
                    image.mipInfos().get(mipIndex).mipPixelHeight(),
                    Buffers.toArray(image.mipData()[mipIndex])
                ));
            }
        }
        return List.copyOf(surfaces);
    }

    private be.twofold.valen.core.texture.TextureFormat toImageFormat(TextureFormat format) {
        // I might not be sure about all these mappings, but it's a start
        return switch (format) {
            case FMT_ALPHA -> be.twofold.valen.core.texture.TextureFormat.R8_UNORM;
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> be.twofold.valen.core.texture.TextureFormat.BC1_UNORM;
            case FMT_BC1_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC1_SRGB;
            case FMT_BC3 -> be.twofold.valen.core.texture.TextureFormat.BC3_UNORM;
            case FMT_BC3_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC3_SRGB;
            case FMT_BC4 -> be.twofold.valen.core.texture.TextureFormat.BC4_UNORM;
            case FMT_BC5 -> be.twofold.valen.core.texture.TextureFormat.BC5_UNORM;
            case FMT_BC6H_UF16 -> be.twofold.valen.core.texture.TextureFormat.BC6H_UFLOAT;
            case FMT_BC7 -> be.twofold.valen.core.texture.TextureFormat.BC7_UNORM;
            case FMT_BC7_SRGB -> be.twofold.valen.core.texture.TextureFormat.BC7_SRGB;
            case FMT_RG16F -> be.twofold.valen.core.texture.TextureFormat.R16G16_SFLOAT;
            case FMT_RG8 -> be.twofold.valen.core.texture.TextureFormat.R8G8_UNORM;
            case FMT_RGBA8 -> be.twofold.valen.core.texture.TextureFormat.R8G8B8A8_UNORM;
            case FMT_X16F -> be.twofold.valen.core.texture.TextureFormat.R16_SFLOAT;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
