package be.twofold.valen.converter;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.reader.image.*;

import java.util.*;

public final class ImageToTexture {
    public Texture convert(Image image) {
        int minMip = image.minMip();
        int width = image.mipInfos().get(minMip).mipPixelWidth();
        int height = image.mipInfos().get(minMip).mipPixelHeight();
        TextureFormat format = toImageFormat(image.header().textureFormat());
        List<Surface> surfaces = convertMipMaps(image);
        boolean isCubeMap = image.header().textureType() == ImageTextureType.TT_3D;

        return new Texture(width, height, format, surfaces, isCubeMap);
    }

    private List<Surface> convertMipMaps(Image image) {
        int faces = image.header().textureType() == ImageTextureType.TT_3D ? 6 : 1;
        int minMip = image.minMip();
        int mipCount = image.mipInfos().size() / faces;

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
            case FMT_ALPHA -> TextureFormat.A8Unorm;
            case FMT_BC1, FMT_BC1_ZERO_ALPHA -> TextureFormat.Bc1Unorm;
            case FMT_BC1_SRGB -> TextureFormat.Bc1UnormSrgb;
            case FMT_BC3 -> TextureFormat.Bc3Unorm;
            case FMT_BC3_SRGB -> TextureFormat.Bc3UnormSrgb;
            case FMT_BC4 -> TextureFormat.Bc4Unorm;
            case FMT_BC5 -> TextureFormat.Bc5Unorm;
            case FMT_BC6H_UF16 -> TextureFormat.Bc6HUf16;
            case FMT_BC7 -> TextureFormat.Bc7Unorm;
            case FMT_BC7_SRGB -> TextureFormat.Bc7UnormSrgb;
            case FMT_RG16F -> TextureFormat.R16G16Float;
            case FMT_RG8 -> TextureFormat.R8G8Unorm;
            case FMT_RGBA8 -> TextureFormat.R8G8B8A8Unorm;
            case FMT_X16F -> TextureFormat.R16Float;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
