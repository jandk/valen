package be.twofold.valen.game.greatcircle.reader.image;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public final class ImageMapper {
    public Texture map(Image image) {
        int minMip = image.minMip();
        int width = minMip < 0 ? image.header().width() : image.sliceInfos().get(minMip).width();
        int height = minMip < 0 ? image.header().height() : image.sliceInfos().get(minMip).height();
        TextureFormat format = toImageFormat(image.header().textureFormat());
        List<Surface> surfaces = convertMipMaps(image);
        boolean isCubeMap = image.header().type() == ImageTextureType.TT_CUBIC;

        return new Texture(width, height, format, isCubeMap, surfaces, image.header().scale(), image.header().bias());
    }

    private List<Surface> convertMipMaps(Image image) {
        int faces = image.header().type() == ImageTextureType.TT_CUBIC ? 6 : 1;
        int mipCount = image.sliceInfos().size() / faces;
        int minMip = image.minMip() < 0 ? mipCount : image.minMip();

        List<Surface> surfaces = new ArrayList<>();
        for (int face = 0; face < faces; face++) {
            for (int mip = minMip; mip < mipCount; mip++) {
                int mipIndex = mip * faces + face;
                if (image.slices()[mipIndex] == null) {
                    // Well, if this happens in a cube map...
                    break;
                }
                surfaces.add(new Surface(
                    image.sliceInfos().get(mipIndex).width(),
                    image.sliceInfos().get(mipIndex).height(),
                    Buffers.toArray(image.slices()[mipIndex])
                ));
            }
        }
        return List.copyOf(surfaces);
    }

    private TextureFormat toImageFormat(ImageTextureFormat format) {
        // I might not be sure about all these mappings, but it's a start
        return switch (format) {
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> TextureFormat.BC1_UNORM;
            case FMT_BC1_SRGB -> TextureFormat.BC1_SRGB;
            case FMT_BC3 -> TextureFormat.BC3_UNORM;
            case FMT_BC3_SRGB -> TextureFormat.BC3_SRGB;
            case FMT_BC4 -> TextureFormat.BC4_UNORM;
            case FMT_BC5 -> TextureFormat.BC5_UNORM;
            case FMT_BC6H_UF16 -> TextureFormat.BC6H_UFLOAT;
            case FMT_BC7 -> TextureFormat.BC7_UNORM;
            case FMT_BC7_SRGB -> TextureFormat.BC7_SRGB;
            case FMT_R8 -> TextureFormat.R8_UNORM;
            case FMT_RG16F -> TextureFormat.R16G16_SFLOAT;
            case FMT_RG8 -> TextureFormat.R8G8_UNORM;
            case FMT_RGBA8 -> TextureFormat.R8G8B8A8_UNORM;
            case FMT_X16 -> TextureFormat.R16_UNORM;
            case FMT_X16F -> TextureFormat.R16_SFLOAT;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
