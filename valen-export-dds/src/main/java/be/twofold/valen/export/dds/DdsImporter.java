package be.twofold.valen.export.dds;

import be.twofold.valen.core.game.*;
import be.twofold.valen.core.io.*;
import be.twofold.valen.core.texture.*;

import java.io.*;
import java.nio.*;
import java.util.*;

public final class DdsImporter implements AssetReader<Texture, Asset> {
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
    public Texture read(BinaryReader reader, Asset asset) throws IOException {
        var headerBuffer = reader
            .readBytes(4 + DdsHeader.SIZE + DdsHeaderDxt10.SIZE) // for magic
            .asBuffer().order(ByteOrder.LITTLE_ENDIAN);
        var header = DdsHeader.fromBuffer(headerBuffer);
        reader.position(4 + DdsHeader.SIZE + (header.header10().isPresent() ? DdsHeaderDxt10.SIZE : 0));

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

        var w = header.width();
        var h = header.height();
        var surfaces = new ArrayList<Surface>();
        for (int i = 0; i < header.mipMapCount(); i++) {
            var data = reader.readBytes(format.surfaceSize(w, h));
            surfaces.add(new Surface(w, h, format, data.toArray()));
            w = Math.max(w / 2, 1);
            h = Math.max(h / 2, 1);
        }

        return new Texture(header.width(), header.height(), format, false, surfaces);
    }

    private TextureFormat mapDxgiFormat(DxgiFormat dxgiFormat) {
        return switch (dxgiFormat) {
            case R10G10B10A2_UNORM -> TextureFormat.R10G10B10A2_UNORM;
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
