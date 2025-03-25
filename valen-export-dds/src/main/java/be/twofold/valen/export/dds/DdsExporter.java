package be.twofold.valen.export.dds;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.texture.*;

import java.io.*;

public final class DdsExporter implements TextureExporter {
    @Override
    public String getID() {
        return "texture.dds";
    }

    @Override
    public String getName() {
        return "DDS (DirectDraw Surface)";
    }

    @Override
    public String getExtension() {
        return "dds";
    }

    @Override
    public Class<Texture> getSupportedType() {
        return Texture.class;
    }

    @Override
    public TextureFormat chooseFormat(TextureFormat format) {
        return switch (format) {
            case R8G8B8_UNORM -> TextureFormat.R8G8B8A8_UNORM;
            case B8G8R8_UNORM -> TextureFormat.B8G8R8A8_UNORM;
            default -> format;
        };
    }

    @Override
    public void export(Texture texture, OutputStream out) throws IOException {
        out.write(createHeader(texture).toBuffer().array());
        for (var surface : texture.surfaces()) {
            out.write(surface.data());
        }
    }

    private DdsHeader createHeader(Texture texture) {
        var format = mapFormat(texture.format());

        var flags = DdsHeader.DDS_HEADER_FLAGS_TEXTURE;
        var height = texture.height();
        var width = texture.width();
        var caps1 = DdsHeader.DDSCAPS_TEXTURE;

        var mipMapCount = texture.surfaces().size() / (texture.isCubeMap() ? 6 : 1);
        if (mipMapCount > 0) {
            flags |= DdsHeader.DDSD_MIPMAPCOUNT;

            if (mipMapCount > 1) {
                caps1 |= DdsHeader.DDSCAPS_COMPLEX | DdsHeader.DDSCAPS_MIPMAP;
            }
        }

        int pitchOrLinearSize;
        if (format.isCompressed()) {
            flags |= DdsHeader.DDSD_LINEARSIZE;
            pitchOrLinearSize = computeLinearSize(texture.width(), texture.height(), format);
        } else {
            flags |= DdsHeader.DDSD_PITCH;
            pitchOrLinearSize = computePitch(texture.width(), format);
        }

        var pixelFormat = createPixelFormat();
        var header10 = createHeaderDxt10(texture, format);

        var caps2 = 0;
        if (texture.isCubeMap()) {
            caps1 |= DdsHeader.DDSCAPS_COMPLEX;
            caps2 |= DdsHeader.DDSCAPS2_CUBEMAP | DdsHeader.DDSCAPS2_CUBEMAP_ALL_FACES;
        }

        return new DdsHeader(flags, height, width, pitchOrLinearSize, 0, mipMapCount, pixelFormat, caps1, caps2, header10);
    }

    private static DxgiFormat mapFormat(TextureFormat format) {
        return switch (format) {
            case R8_UNORM -> DxgiFormat.R8_UNORM;
            case R8G8_UNORM -> DxgiFormat.R8G8_UNORM;
            case R8G8B8_UNORM -> DxgiFormat.R8G8B8A8_UNORM;
            case R8G8B8A8_UNORM -> DxgiFormat.R8G8B8A8_UNORM;
            case B8G8R8_UNORM -> DxgiFormat.B8G8R8A8_UNORM;
            case B8G8R8A8_UNORM -> DxgiFormat.B8G8R8A8_UNORM;
            case R16_UNORM -> DxgiFormat.R16_UNORM;
            case R16G16B16A16_UNORM -> DxgiFormat.R16G16B16A16_UNORM;
            case R16_SFLOAT -> DxgiFormat.R16_FLOAT;
            case R16G16_SFLOAT -> DxgiFormat.R16G16_FLOAT;
            case R16G16B16A16_SFLOAT -> DxgiFormat.R16G16B16A16_FLOAT;
            case R32_SFLOAT -> DxgiFormat.R32_FLOAT;
            case R32G32_SFLOAT -> DxgiFormat.R32G32_FLOAT;
            case R32G32B32A32_SFLOAT -> DxgiFormat.R32G32B32A32_FLOAT;
            case BC1_UNORM -> DxgiFormat.BC1_UNORM;
            case BC1_SRGB -> DxgiFormat.BC1_UNORM_SRGB;
            case BC2_UNORM -> DxgiFormat.BC2_UNORM;
            case BC2_SRGB -> DxgiFormat.BC2_UNORM_SRGB;
            case BC3_UNORM -> DxgiFormat.BC3_UNORM;
            case BC3_SRGB -> DxgiFormat.BC3_UNORM_SRGB;
            case BC4_UNORM -> DxgiFormat.BC4_UNORM;
            case BC4_SNORM -> DxgiFormat.BC4_SNORM;
            case BC5_UNORM -> DxgiFormat.BC5_UNORM;
            case BC5_SNORM -> DxgiFormat.BC5_SNORM;
            case BC6H_UFLOAT -> DxgiFormat.BC6H_UF16;
            case BC6H_SFLOAT -> DxgiFormat.BC6H_SF16;
            case BC7_UNORM -> DxgiFormat.BC7_UNORM;
            case BC7_SRGB -> DxgiFormat.BC7_UNORM_SRGB;
        };
    }

    private DdsPixelFormat createPixelFormat() {
        var flags = DdsPixelFormat.DDPF_FOURCC;
        var fourCC = 'D' | 'X' << 8 | '1' << 16 | '0' << 24;
        return new DdsPixelFormat(flags, fourCC, 0, 0, 0, 0, 0);
    }

    private DdsHeaderDxt10 createHeaderDxt10(Texture texture, DxgiFormat format) {
        var miscFlag = texture.isCubeMap() ? DdsHeaderDxt10.DDS_RESOURCE_MISC_TEXTURECUBE : 0;
        return new DdsHeaderDxt10(
            format.getCode(),
            DdsHeaderDxt10.DDS_DIMENSION_TEXTURE2D,
            miscFlag,
            1,
            DdsHeaderDxt10.DDS_ALPHA_MODE_UNKNOWN
        );
    }

    private int computeLinearSize(int width, int height, DxgiFormat format) {
        // Round up to next multiple of 4
        var blocksX = Math.max(1, (width + 3) / 4);
        var blocksY = Math.max(1, (height + 3) / 4);

        switch (format) {
            case BC1_UNORM, BC1_UNORM_SRGB,
                 BC4_UNORM, BC4_SNORM -> {
                var pitch = blocksX * 8;
                return pitch * blocksY;
            }
            case BC2_UNORM, BC2_UNORM_SRGB,
                 BC3_UNORM, BC3_UNORM_SRGB,
                 BC5_UNORM, BC5_SNORM,
                 BC6H_UF16, BC6H_SF16,
                 BC7_UNORM, BC7_UNORM_SRGB -> {
                var pitch = blocksX * 16;
                return pitch * blocksY;
            }
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }

    private int computePitch(int width, DxgiFormat format) {
        return switch (format) {
            case A8_UNORM, R8_UNORM -> width;
            case R16G16_FLOAT, R8G8B8A8_UNORM -> width * 4;
            case R16_FLOAT, R8G8_UNORM -> width * 2;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
