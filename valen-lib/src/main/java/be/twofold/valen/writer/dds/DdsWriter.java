package be.twofold.valen.writer.dds;

import be.twofold.valen.core.texture.*;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;

public final class DdsWriter {
    private final WritableByteChannel channel;

    public DdsWriter(WritableByteChannel channel) {
        this.channel = channel;
    }

    public void write(Texture texture) throws IOException {
        channel.write(createHeader(texture).toBuffer());
        for (var surface : texture.surfaces()) {
            channel.write(ByteBuffer.wrap(surface.data()));
        }
    }

    private DdsHeader createHeader(Texture texture) {
        var format = toDxgiFormat(texture.format());

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

    private static DdsPixelFormat createPixelFormat() {
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

    private DxgiFormat toDxgiFormat(TextureFormat format) {
        return switch (format) {
            case R8G8B8A8UNorm -> DxgiFormat.R8G8B8A8_UNORM;
            case R16G16Float -> DxgiFormat.R16G16_FLOAT;
            case R8G8UNorm -> DxgiFormat.R8G8_UNORM;
            case R16Float -> DxgiFormat.R16_FLOAT;
            case A8UNorm -> DxgiFormat.A8_UNORM;
            case Bc1UNorm -> DxgiFormat.BC1_UNORM;
            case Bc1UNormSrgb -> DxgiFormat.BC1_UNORM_SRGB;
            case Bc2UNorm -> DxgiFormat.BC2_UNORM;
            case Bc2UNormSrgb -> DxgiFormat.BC2_UNORM_SRGB;
            case Bc3UNorm -> DxgiFormat.BC3_UNORM;
            case Bc3UNormSrgb -> DxgiFormat.BC3_UNORM_SRGB;
            case Bc4UNorm -> DxgiFormat.BC4_UNORM;
            case Bc4SNorm -> DxgiFormat.BC4_SNORM;
            case Bc5UNorm -> DxgiFormat.BC5_UNORM;
            case Bc5SNorm -> DxgiFormat.BC5_SNORM;
            case Bc6HUFloat16 -> DxgiFormat.BC6H_UF16;
            case Bc6HSFloat16 -> DxgiFormat.BC6H_SF16;
            case Bc7UNorm -> DxgiFormat.BC7_UNORM;
            case Bc7UNormSrgb -> DxgiFormat.BC7_UNORM_SRGB;
        };
    }
}
