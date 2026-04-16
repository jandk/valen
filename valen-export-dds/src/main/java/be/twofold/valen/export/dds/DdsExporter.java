package be.twofold.valen.export.dds;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.texture.*;

import java.io.*;

public final class DdsExporter extends TextureExporter {
    public DdsExporter() {
        super(true);
    }

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
    protected TextureFormat chooseFormat(TextureFormat format) {
        return switch (format) {
            case R8G8B8_UNORM -> TextureFormat.R8G8B8A8_UNORM;
            case B8G8R8_UNORM -> TextureFormat.B8G8R8A8_UNORM;
            case R8G8B8_SRGB -> TextureFormat.R8G8B8A8_SRGB;
            case B8G8R8_SRGB -> TextureFormat.B8G8R8A8_SRGB;
            case R16G16B16_SFLOAT -> TextureFormat.R16G16B16A16_SFLOAT;
            default -> format;
        };
    }

    @Override
    protected void doExport(Texture texture, OutputStream out) throws IOException {
        out.write(createHeader(texture).toBuffer().array());
        for (var surface : texture.surfaces()) {
            try (var in = surface.data().asInputStream()) {
                in.transferTo(out);
            }
        }
    }

    private DdsHeader createHeader(Texture texture) {
        var format = mapFormat(texture.format());

        var flags = DdsHeader.DDS_HEADER_FLAGS_TEXTURE;
        var height = texture.height();
        var width = texture.width();
        var caps1 = DdsHeader.DDSCAPS_TEXTURE;

        var mipCount = texture.mipCount();
        if (mipCount > 0) {
            flags |= DdsHeader.DDSD_MIPMAPCOUNT;

            if (mipCount > 1) {
                caps1 |= DdsHeader.DDSCAPS_COMPLEX | DdsHeader.DDSCAPS_MIPMAP;
            }
        }

        int pitchOrLinearSize = computePitchOrLinearSize(texture.width(), texture.height(), texture.depthOrLayers(), format);
        flags |= format.isCompressed() ? DdsHeader.DDSD_LINEARSIZE : DdsHeader.DDSD_PITCH;

        var pixelFormat = createPixelFormat();
        var header10 = createHeaderDxt10(texture, format);

        var depth = 0;
        var caps2 = 0;
        switch (texture.kind()) {
            case TEXTURE_3D -> {
                flags |= DdsHeader.DDSD_DEPTH;
                caps1 |= DdsHeader.DDSCAPS_COMPLEX;
                caps2 |= DdsHeader.DDSCAPS2_VOLUME;
                depth = texture.depthOrLayers();
            }
            case CUBE_MAP -> {
                caps1 |= DdsHeader.DDSCAPS_COMPLEX;
                caps2 |= DdsHeader.DDSCAPS2_CUBEMAP | DdsHeader.DDSCAPS2_CUBEMAP_ALL_FACES;
            }
        }

        return new DdsHeader(flags, height, width, pitchOrLinearSize, depth, mipCount, pixelFormat, caps1, caps2, header10);
    }

    private static DxgiFormat mapFormat(TextureFormat format) {
        return switch (format) {
            case R8_UNORM,
                 R8_SRGB -> DxgiFormat.R8_UNORM;
            case R8G8_UNORM -> DxgiFormat.R8G8_UNORM;
            case R8G8B8_UNORM,
                 R8G8B8A8_UNORM -> DxgiFormat.R8G8B8A8_UNORM;
            case R8G8B8_SRGB,
                 R8G8B8A8_SRGB -> DxgiFormat.R8G8B8A8_UNORM_SRGB;
            case B8G8R8_UNORM,
                 B8G8R8A8_UNORM -> DxgiFormat.B8G8R8A8_UNORM;
            case B8G8R8_SRGB,
                 B8G8R8A8_SRGB -> DxgiFormat.B8G8R8A8_UNORM_SRGB;
            case R16_UNORM -> DxgiFormat.R16_UNORM;
            case R16G16B16A16_UNORM -> DxgiFormat.R16G16B16A16_UNORM;
            case R16_SFLOAT -> DxgiFormat.R16_FLOAT;
            case R16G16_SFLOAT -> DxgiFormat.R16G16_FLOAT;
            case R16G16B16_SFLOAT,
                 R16G16B16A16_SFLOAT -> DxgiFormat.R16G16B16A16_FLOAT;
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
        var dimension = switch (texture.kind()) {
            case TEXTURE_1D -> DdsHeaderDxt10.DDS_DIMENSION_TEXTURE1D;
            case TEXTURE_2D, CUBE_MAP -> DdsHeaderDxt10.DDS_DIMENSION_TEXTURE2D;
            case TEXTURE_3D -> DdsHeaderDxt10.DDS_DIMENSION_TEXTURE3D;
        };
        var miscFlag = texture.kind() == TextureKind.CUBE_MAP ? DdsHeaderDxt10.DDS_RESOURCE_MISC_TEXTURECUBE : 0;
        var arraySize = switch (texture.kind()) {
            case TEXTURE_1D, TEXTURE_2D -> texture.depthOrLayers();
            case TEXTURE_3D -> 1;
            case CUBE_MAP -> texture.depthOrLayers() / 6;
        };
        return new DdsHeaderDxt10(
            format.getCode(),
            dimension,
            miscFlag,
            arraySize,
            DdsHeaderDxt10.DDS_ALPHA_MODE_UNKNOWN
        );
    }

    private int computePitchOrLinearSize(int width, int height, int depth, DxgiFormat format) {
        var blocksX = Math.ceilDiv(width, 4);
        var blocksY = Math.ceilDiv(height, 4);

        return switch (format) {
            // For compressed formats, pitch is the size of the compressed block
            case BC1_UNORM, BC1_UNORM_SRGB,
                 BC4_UNORM, BC4_SNORM -> blocksX * blocksY * depth * 8;
            case BC2_UNORM, BC2_UNORM_SRGB,
                 BC3_UNORM, BC3_UNORM_SRGB,
                 BC5_UNORM, BC5_SNORM,
                 BC6H_UF16, BC6H_SF16,
                 BC7_UNORM, BC7_UNORM_SRGB -> blocksX * blocksY * depth * 16;
            // For uncompressed formats, pitch is the size of a row in bytes
            default -> (width * format.bitsPerPixel() + 7) / 8;
        };
    }
}
