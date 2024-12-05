package be.twofold.valen.export.dds;

import be.twofold.valen.core.export.*;
import be.twofold.valen.core.texture.*;

import java.io.*;
import java.util.*;

public final class DdsExporter implements Exporter<Texture> {
    private static final Map<TextureFormat, DxgiFormat> FORMATS = Map.ofEntries(
        Map.entry(TextureFormat.R8G8B8A8_UNORM, DxgiFormat.R8G8B8A8_UNORM),
        Map.entry(TextureFormat.R16G16_SFLOAT, DxgiFormat.R16G16_FLOAT),
        Map.entry(TextureFormat.R8G8_UNORM, DxgiFormat.R8G8_UNORM),
        Map.entry(TextureFormat.R16_SFLOAT, DxgiFormat.R16_FLOAT),
        Map.entry(TextureFormat.R8_UNORM, DxgiFormat.A8_UNORM),
        Map.entry(TextureFormat.BC1_UNORM, DxgiFormat.BC1_UNORM),
        Map.entry(TextureFormat.BC1_SRGB, DxgiFormat.BC1_UNORM_SRGB),
        Map.entry(TextureFormat.BC2_UNORM, DxgiFormat.BC2_UNORM),
        Map.entry(TextureFormat.BC2_SRGB, DxgiFormat.BC2_UNORM_SRGB),
        Map.entry(TextureFormat.BC3_UNORM, DxgiFormat.BC3_UNORM),
        Map.entry(TextureFormat.BC3_SRGB, DxgiFormat.BC3_UNORM_SRGB),
        Map.entry(TextureFormat.BC4_UNORM, DxgiFormat.BC4_UNORM),
        Map.entry(TextureFormat.BC4_SNORM, DxgiFormat.BC4_SNORM),
        Map.entry(TextureFormat.BC5_UNORM, DxgiFormat.BC5_UNORM),
        Map.entry(TextureFormat.BC5_SNORM, DxgiFormat.BC5_SNORM),
        Map.entry(TextureFormat.BC6H_UFLOAT, DxgiFormat.BC6H_UF16),
        Map.entry(TextureFormat.BC6H_SFLOAT, DxgiFormat.BC6H_SF16),
        Map.entry(TextureFormat.BC7_UNORM, DxgiFormat.BC7_UNORM),
        Map.entry(TextureFormat.BC7_SRGB, DxgiFormat.BC7_UNORM_SRGB)
    );

    @Override
    public String getExtension() {
        return "dds";
    }

    @Override
    public Class<Texture> getSupportedType() {
        return Texture.class;
    }

    @Override
    public void export(Texture texture, OutputStream out) throws IOException {
        out.write(createHeader(texture).toBuffer().array());
        for (var surface : texture.surfaces()) {
            out.write(surface.data());
        }
    }

    private DdsHeader createHeader(Texture texture) {
        var format = FORMATS.get(texture.format());

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
            case A8_UNORM -> width;
            case R16G16_FLOAT, R8G8B8A8_UNORM -> width * 4;
            case R16_FLOAT, R8G8_UNORM -> width * 2;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
