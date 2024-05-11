package be.twofold.valen.reader.image;

import be.twofold.valen.core.texture.*;

import java.util.*;

public final class ImageMapper {
    public Texture map(Image image) {
        int minMip = image.minMip();
        int width = minMip < 0 ? image.header().pixelWidth() : image.mipInfos().get(minMip).mipPixelWidth();
        int height = minMip < 0 ? image.header().pixelHeight() : image.mipInfos().get(minMip).mipPixelHeight();
        TextureFormat format = toImageFormat(image.header().textureFormat());
        List<Surface> surfaces = convertMipMaps(image);
        boolean isCubeMap = image.header().textureType() == ImageTextureType.TT_CUBIC;

        return new Texture(width, height, format, surfaces, isCubeMap);
    }

    private List<Surface> convertMipMaps(Image image) {
        int faces = image.header().textureType() == ImageTextureType.TT_CUBIC ? 6 : 1;
        int mipCount = image.mipInfos().size() / faces;
        int minMip = image.minMip() < 0 ? mipCount : image.minMip();

        List<Surface> surfaces = new ArrayList<>();
        for (int face = 0; face < faces; face++) {
            for (int mip = minMip; mip < mipCount; mip++) {
                int mipIndex = mip * faces + face;
                surfaces.add(new Surface(
                    image.mipInfos().get(mipIndex).mipPixelWidth(),
                    image.mipInfos().get(mipIndex).mipPixelHeight(),
                    image.mipData()[mipIndex]
                ));
            }
        }
        return List.copyOf(surfaces);
    }

    private TextureFormat toImageFormat(ImageTextureFormat format) {
        // I might not be sure about all these mappings, but it's a start
        return switch (format) {
            case FMT_ALPHA -> TextureFormat.A8UNorm;
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> TextureFormat.Bc1UNorm;
            case FMT_BC1_SRGB -> TextureFormat.Bc1UNormSrgb;
            case FMT_BC3 -> TextureFormat.Bc3UNorm;
            case FMT_BC3_SRGB -> TextureFormat.Bc3UNormSrgb;
            case FMT_BC4 -> TextureFormat.Bc4UNorm;
            case FMT_BC5 -> TextureFormat.Bc5UNorm;
            case FMT_BC6H_UF16 -> TextureFormat.Bc6HUFloat16;
            case FMT_BC7 -> TextureFormat.Bc7UNorm;
            case FMT_BC7_SRGB -> TextureFormat.Bc7UNormSrgb;
            case FMT_RG16F -> TextureFormat.R16G16Float;
            case FMT_RG8 -> TextureFormat.R8G8UNorm;
            case FMT_RGBA8 -> TextureFormat.R8G8B8A8UNorm;
            case FMT_X16F -> TextureFormat.R16Float;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
