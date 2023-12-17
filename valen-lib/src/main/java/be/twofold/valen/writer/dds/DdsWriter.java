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
        for (Surface surface : texture.surfaces()) {
            channel.write(ByteBuffer.wrap(surface.data()));
        }
    }

    private DdsHeader createHeader(Texture texture) {
        int flags = DdsHeader.DDS_HEADER_FLAGS_TEXTURE;
        int height = texture.height();
        int width = texture.width();
        int caps1 = DdsHeader.DDSCAPS_TEXTURE;

        int mipMapCount = texture.surfaces().size() / (texture.isCubeMap() ? 6 : 1);
        if (mipMapCount > 0) {
            flags |= DdsHeader.DDSD_MIPMAPCOUNT;

            if (mipMapCount > 1) {
                caps1 |= DdsHeader.DDSCAPS_COMPLEX | DdsHeader.DDSCAPS_MIPMAP;
            }
        }

        int pitchOrLinearSize;
        if (texture.format().isCompressed()) {
            flags |= DdsHeader.DDSD_LINEARSIZE;
            pitchOrLinearSize = computeLinearSize(texture.width(), texture.height(), texture.format());
        } else {
            flags |= DdsHeader.DDSD_PITCH;
            pitchOrLinearSize = computePitch(texture.width(), texture.format());
        }

        DdsPixelFormat pixelFormat = createPixelFormat();
        DdsHeaderDxt10 header10 = createHeaderDxt10(texture);

        int caps2 = 0;
        if (texture.isCubeMap()) {
            caps1 |= DdsHeader.DDSCAPS_COMPLEX;
            caps2 |= DdsHeader.DDSCAPS2_CUBEMAP | DdsHeader.DDSCAPS2_CUBEMAP_ALL_FACES;
        }

        return new DdsHeader(flags, height, width, pitchOrLinearSize, 0, mipMapCount, pixelFormat, caps1, caps2, header10);
    }

    private DdsPixelFormat createPixelFormat() {
        int fourCC = 'D' | 'X' << 8 | '1' << 16 | '0' << 24;
        int flags = DdsPixelFormat.DDPF_FOURCC;
        return new DdsPixelFormat(flags, fourCC, 0, 0, 0, 0, 0);
    }

    private DdsHeaderDxt10 createHeaderDxt10(Texture texture) {
        int dxgiFormat = texture.format().getCode();
        int miscFlag = texture.isCubeMap() ? DdsHeaderDxt10.DDS_RESOURCE_MISC_TEXTURECUBE : 0;
        return new DdsHeaderDxt10(
            dxgiFormat,
            DdsHeaderDxt10.DDS_DIMENSION_TEXTURE2D,
            miscFlag,
            1,
            DdsHeaderDxt10.DDS_ALPHA_MODE_UNKNOWN
        );
    }

    private static int computeLinearSize(int width, int height, TextureFormat format) {
        // Round up to next multiple of 4
        int blocksX = Math.max(1, (width + 3) / 4);
        int blocksY = Math.max(1, (height + 3) / 4);

        switch (format) {
            case Bc1Typeless, Bc1Unorm, Bc1UnormSrgb,
                Bc4Typeless, Bc4Unorm, Bc4Snorm -> {
                int pitch = blocksX * 8;
                return pitch * blocksY;
            }
            case Bc2Typeless, Bc2Unorm, Bc2UnormSrgb,
                Bc3Typeless, Bc3Unorm, Bc3UnormSrgb,
                Bc5Typeless, Bc5Unorm, Bc5Snorm,
                Bc6HTypeless, Bc6HUf16, Bc6HSf16,
                Bc7Typeless, Bc7Unorm, Bc7UnormSrgb -> {
                int pitch = blocksX * 16;
                return pitch * blocksY;
            }
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }

    private static int computePitch(int width, TextureFormat format) {
        return switch (format) {
            case A8Unorm -> width;
            case R16G16Float, R8G8B8A8Unorm -> width * 4;
            case R16Float, R8G8Unorm -> width * 2;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }
}
