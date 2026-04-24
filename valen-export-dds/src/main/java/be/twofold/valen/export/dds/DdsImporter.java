package be.twofold.valen.export.dds;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.function.*;

public final class DdsImporter implements AssetReader.Binary<Texture, Asset> {
    private DdsImporter() {
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <A extends Asset> AssetReader<Texture, A> create() {
        return (AssetReader) new DdsImporter();
    }

    @Override
    public boolean canRead(Asset asset) {
        return asset.id().extension().equalsIgnoreCase("dds");
    }

    @Override
    public Texture read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
        var headerBuffer = source
            .readBytes(4 + DdsHeader.SIZE + DdsHeaderDxt10.SIZE)
            .asBuffer().order(ByteOrder.LITTLE_ENDIAN);
        var header = DdsHeader.fromBuffer(headerBuffer);
        source.position(4 + DdsHeader.SIZE + (header.header10().isPresent() ? DdsHeaderDxt10.SIZE : 0));

        if (!header.pixelFormat().flags().contains(DdsPixelFormatFlags.DDPF_FOURCC)) {
            throw new DdsException("Only supports DDPF_FOURCC");
        }

        var format = switch (header.pixelFormat().fourCC()) {
            case DXT1 -> TextureFormat.BC1_UNORM;
            case DXT2, DXT3 -> TextureFormat.BC2_UNORM;
            case DXT4, DXT5 -> TextureFormat.BC3_UNORM;
            case BC4U -> TextureFormat.BC4_UNORM;
            case BC4S -> TextureFormat.BC4_SNORM;
            case BC5U -> TextureFormat.BC5_UNORM;
            case BC5S -> TextureFormat.BC5_SNORM;
            case DX10 -> mapDxgiFormat(header.header10().orElseThrow().dxgiFormat());
            default -> throw new UnsupportedOperationException("Unsupported fourCC: " + header.pixelFormat().fourCC());
        };

        var kind = detectKind(header);
        var width = header.width();
        var height = header.height();
        var mipCount = Math.max(header.mipMapCount(), 1);
        var depthOrLayers = switch (kind) {
            case TEXTURE_3D -> Math.max(header.depth(), 1);
            case CUBE_MAP -> 6;
            default -> header.header10().map(DdsHeaderDxt10::arraySize).orElse(1);
        };

        var surfaces = new ArrayList<Surface>();
        if (kind == TextureKind.TEXTURE_3D) {
            var w = width;
            var h = height;
            var d = depthOrLayers;
            for (int mip = 0; mip < mipCount; mip++) {
                surfaces.add(new Surface(format, w, h, d, source.readBytes(format.surfaceSize(w, h, d))));
                w = Math.max(1, w / 2);
                h = Math.max(1, h / 2);
                d = Math.max(1, d / 2);
            }
        } else {
            for (int layer = 0; layer < depthOrLayers; layer++) {
                var w = width;
                var h = height;
                for (int mip = 0; mip < mipCount; mip++) {
                    surfaces.add(new Surface(format, w, h, 1, source.readBytes(format.surfaceSize(w, h, 1))));
                    w = Math.max(1, w / 2);
                    h = Math.max(1, h / 2);
                }
            }
        }

        return new Texture(format, kind, width, height, depthOrLayers, surfaces, UnaryOperator.identity());
    }

    private static TextureKind detectKind(DdsHeader header) {
        if (header.header10().isPresent()) {
            var header10 = header.header10().get();
            return switch (header10.resourceDimension()) {
                case DdsHeaderDxt10.DDS_DIMENSION_TEXTURE1D -> TextureKind.TEXTURE_1D;
                case DdsHeaderDxt10.DDS_DIMENSION_TEXTURE3D -> TextureKind.TEXTURE_3D;
                default -> (header10.miscFlag() & DdsHeaderDxt10.DDS_RESOURCE_MISC_TEXTURECUBE) != 0
                    ? TextureKind.CUBE_MAP
                    : TextureKind.TEXTURE_2D;
            };
        }
        if (header.caps2().contains(DdsHeaderCaps2.DDSCAPS2_VOLUME)) {
            return TextureKind.TEXTURE_3D;
        }
        if (header.caps2().contains(DdsHeaderCaps2.DDSCAPS2_CUBEMAP)) {
            return TextureKind.CUBE_MAP;
        }
        return TextureKind.TEXTURE_2D;
    }

    private static TextureFormat mapDxgiFormat(DxgiFormat dxgiFormat) {
        return switch (dxgiFormat) {
            case R10G10B10A2_UNORM -> TextureFormat.R10G10B10A2_UNORM;
            case R11G11B10_FLOAT -> TextureFormat.R11G11B10_SFLOAT;
            case R16_FLOAT -> TextureFormat.R16_SFLOAT;
            case R16G16_FLOAT -> TextureFormat.R16G16_SFLOAT;
            case R16G16B16A16_FLOAT -> TextureFormat.R16G16B16A16_SFLOAT;

            case BC1_TYPELESS, BC1_UNORM -> TextureFormat.BC1_UNORM;
            case BC1_UNORM_SRGB -> TextureFormat.BC1_SRGB;
            case BC2_TYPELESS, BC2_UNORM -> TextureFormat.BC2_UNORM;
            case BC2_UNORM_SRGB -> TextureFormat.BC2_SRGB;
            case BC3_TYPELESS, BC3_UNORM -> TextureFormat.BC3_UNORM;
            case BC3_UNORM_SRGB -> TextureFormat.BC3_SRGB;
            case BC4_TYPELESS, BC4_UNORM -> TextureFormat.BC4_UNORM;
            case BC4_SNORM -> TextureFormat.BC4_SNORM;
            case BC5_TYPELESS, BC5_UNORM -> TextureFormat.BC5_UNORM;
            case BC5_SNORM -> TextureFormat.BC5_SNORM;
            case BC6H_TYPELESS, BC6H_UF16 -> TextureFormat.BC6H_UFLOAT;
            case BC6H_SF16 -> TextureFormat.BC6H_SFLOAT;
            case BC7_TYPELESS, BC7_UNORM -> TextureFormat.BC7_UNORM;
            case BC7_UNORM_SRGB -> TextureFormat.BC7_SRGB;

            default -> throw new UnsupportedOperationException("Unsupported DXGI format: " + dxgiFormat);
        };
    }
}
