package be.twofold.valen.writer.dds;

import java.nio.*;

final class DdsHeader {
    private int flags = 0x00001007; // DDSD_CAPS | DDSD_HEIGHT | DDSD_WIDTH | DDSD_PIXELFORMAT
    private int height;
    private int width;
    private int pitchOrLinearSize;
    private int depth;
    private int mipMapCount;
    private final DdsPixelFormat pixelFormat = new DdsPixelFormat();
    private int caps1 = 0x00001000; // DDSCAPS_TEXTURE
    private int caps2;
    private final DdsHeader10 header10 = new DdsHeader10();

    private static final int Size = 0x7c;

    static DdsHeader create(DdsInfo info) {
        DdsHeader header = new DdsHeader();
        header.width = info.width();
        header.height = info.height();
        header.depth = 1;

        if (info.mipCount() > 0) {
            header.mipMapCount = info.mipCount();
            header.flags |= 0x00020000; // DDSD_MIPMAPCOUNT

            if (info.mipCount() > 1) {
                header.caps1 |= 0x00400008; // DDSCAPS_MIPMAP | DDSCAPS_COMPLEX
            }
        }

        if (info.format().isCompressed()) {
            header.flags |= 0x00080000; // DDSD_LINEARSIZE
            header.pitchOrLinearSize = computeLinearSize(info.width(), info.height(), info.format());
        } else {
            header.flags |= 0x00000008; // DDSD_PITCH
            header.pitchOrLinearSize = computePitch(info.width(), info.format());
        }

        // Set fourcc to dx10
        header.pixelFormat.fourCC = 0x30315844; // "DX10"
        header.pixelFormat.flags |= 0x00000004; // DDPF_FOURCC

        header.header10.dxgiFormat = info.format();
        header.header10.resourceDimension = 0x00000003; // D3D10_RESOURCE_DIMENSION_TEXTURE2D
        header.header10.arraySize = 1;

        if (info.isCubeMap()) {
            header.caps1 |= 0x00000008; // DDSCAPS_COMPLEX
            header.caps2 |= 0x0000fe00; // DDSCAPS2_CUBEMAP | DDSCAPS2_CUBEMAP_ALL_FACES

            header.header10.miscFlag |= 0x00000004; // DDS_RESOURCE_MISC_TEXTURECUBE
        }

        return header;
    }

    private static int computeLinearSize(int width, int height, DxgiFormat format) {
        // Round up to next multiple of 4
        int blocksX = Math.max(1, (width + 3) / 4);
        int blocksY = Math.max(1, (height + 3) / 4);

        switch (format) {
            case BC1_TYPELESS, BC1_UNORM, BC1_UNORM_SRGB,
                BC4_TYPELESS, BC4_UNORM, BC4_SNORM -> {
                return blocksX * blocksY * 8;
            }
            case BC2_TYPELESS, BC2_UNORM, BC2_UNORM_SRGB,
                BC3_TYPELESS, BC3_UNORM, BC3_UNORM_SRGB,
                BC5_TYPELESS, BC5_UNORM, BC5_SNORM,
                BC6H_TYPELESS, BC6H_UF16, BC6H_SF16,
                BC7_TYPELESS, BC7_UNORM, BC7_UNORM_SRGB -> {
                return blocksX * blocksY * 16;
            }
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        }
    }

    private static int computePitch(int width, DxgiFormat format) {
        return switch (format) {
            case A8_UNORM -> width;
            case R16G16_FLOAT, R8G8B8A8_UNORM -> width * 4;
            case R8G8_UNORM -> width * 2;
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        };
    }

    ByteBuffer toBuffer() {
        return ByteBuffer.allocate(4 + Size + DdsHeader10.Size)
            .order(ByteOrder.LITTLE_ENDIAN)
            .putInt(0x20534444) // "DDS "
            .putInt(Size) // size
            .putInt(flags)
            .putInt(height)
            .putInt(width)
            .putInt(pitchOrLinearSize)
            .putInt(depth)
            .putInt(mipMapCount)
            .position(0x4c) // skip 11 ints
            .put(pixelFormat.toBuffer())
            .putInt(caps1)
            .putInt(caps2)
            .putInt(0) // caps3
            .putInt(0) // caps4
            .putInt(0) // reserved
            .put(header10.toBuffer())
            .flip();
    }
}
