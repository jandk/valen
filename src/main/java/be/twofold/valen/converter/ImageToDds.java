package be.twofold.valen.converter;

import be.twofold.valen.reader.image.*;
import be.twofold.valen.writer.dds.*;

import java.util.*;

public class ImageToDds {
    public Dds convert(Image image) {
        DdsInfo info = createHeader(image);
        byte[][] mipMaps = createMipMapArray(image);
        return new Dds(info, mipMaps);
    }

    private DdsInfo createHeader(Image image) {
        int minMip = image.minMip();
        int width = image.mips().get(minMip).mipPixelWidth();
        int height = image.mips().get(minMip).mipPixelHeight();
        int mipCount = image.header().mipCount() - minMip;
        DxgiFormat format = toDxgiFormat(image.header().textureFormat());
        boolean isCubeMap = image.header().textureType() == ImageTextureType.TT_CUBIC;

        return new DdsInfo(width, height, mipCount, format, isCubeMap);
    }

    private byte[][] createMipMapArray(Image image) {
        List<byte[]> mipMaps = new ArrayList<>();

        // DDS images are stored transposed compared to BIM images
        int faces = image.header().textureType() == ImageTextureType.TT_CUBIC ? 6 : 1;
        for (int face = 0; face < faces; face++) {
            for (int mip = image.minMip(); mip < image.header().mipCount(); mip++) {
                mipMaps.add(image.mipData()[mip * faces + face]);
            }
        }
        return mipMaps.toArray(new byte[0][]);
    }

    private static DxgiFormat toDxgiFormat(ImageTextureFormat format) {
        // I might not be sure about all these mappings, but it's a start
        return switch (format) {
            case FMT_ALPHA -> DxgiFormat.A8_UNORM;
            case FMT_BC1 -> DxgiFormat.BC1_UNORM;
            case FMT_BC1_SRGB -> DxgiFormat.BC1_UNORM_SRGB;
            case FMT_BC1_ZERO_ALPHA -> DxgiFormat.BC1_TYPELESS;
            case FMT_BC3 -> DxgiFormat.BC3_UNORM;
            case FMT_BC3_SRGB -> DxgiFormat.BC3_UNORM_SRGB;
            case FMT_BC4 -> DxgiFormat.BC4_UNORM;
            case FMT_BC5 -> DxgiFormat.BC5_UNORM;
            case FMT_BC6H_UF16 -> DxgiFormat.BC6H_UF16;
            case FMT_BC7 -> DxgiFormat.BC7_UNORM;
            case FMT_BC7_SRGB -> DxgiFormat.BC7_UNORM_SRGB;
            case FMT_RG16F -> DxgiFormat.R16G16_FLOAT;
            case FMT_RG8 -> DxgiFormat.R8G8_UNORM;
            case FMT_RGBA8 -> DxgiFormat.R8G8B8A8_UNORM;
            case FMT_X16F -> DxgiFormat.R16_FLOAT;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
