package be.twofold.valen.game.darkages.reader.image;

import be.twofold.valen.core.io.*;
import be.twofold.valen.game.idtech.defines.*;

import java.io.*;

public record ImageHeader(
    byte version,
    TextureType textureType,
    TextureMaterialKind textureMaterialKind,
    int pixelWidth,
    int pixelHeight,
    int depth,
    int mipCount,
    int unkFlags,
    float albedoSpecularBias,
    float albedoSpecularScale,
    TextureFormat textureFormat,
    boolean streamed,
    boolean singleStream,
    boolean noMips,
    boolean fftBloom,
    byte unknown,
    int streamDBMipCount
) {
    public static ImageHeader read(BinaryReader reader) throws IOException {
        reader.expectByte((byte) 'B');
        reader.expectByte((byte) 'I');
        reader.expectByte((byte) 'M');
        var version = reader.readByte();

        var textureType = TextureType.fromValue(reader.readInt());
        var textureMaterialKind = toMaterialKind(reader.readInt());
        var pixelWidth = reader.readInt();
        var pixelHeight = reader.readInt();
        var depth = reader.readInt();
        var mipCount = reader.readInt();
        var unkFlags = reader.readInt();
        var albedoSpecularBias = reader.readFloat();
        var albedoSpecularScale = reader.readFloat();
        reader.expectByte((byte) 0);
        var textureFormat = toTextureFormat(reader.readInt());
        reader.expectInt(8); // always 8
        reader.expectInt(0); // padding
        reader.expectShort((short) 0); // padding
        var streamed = reader.readBoolByte();
        var singleStream = reader.readBoolByte();
        var noMips = reader.readBoolByte();
        var fftBloom = reader.readBoolByte();
        byte unknown = version >= 24 ? reader.readByte() : 0;
        var streamDBMipCount = reader.readInt();

        return new ImageHeader(
            version,
            textureType,
            textureMaterialKind,
            pixelWidth,
            pixelHeight,
            depth,
            mipCount,
            unkFlags,
            albedoSpecularBias,
            albedoSpecularScale,
            textureFormat,
            streamed,
            singleStream,
            noMips,
            fftBloom,
            unknown,
            streamDBMipCount
        );
    }

    public int startMip() {
        var mask = switch (textureType) {
            case TT_2D, TT_3D -> 0x0f;
            case TT_CUBIC -> 0xff;
        };
        return streamDBMipCount & mask;
    }

    public int totalMipCount() {
        var faces = switch (textureType) {
            case TT_2D, TT_3D -> 1;
            case TT_CUBIC -> 6;
        };
        return mipCount * faces;
    }

    private static TextureMaterialKind toMaterialKind(int value) {
        return switch (value) {
            case +0 -> TextureMaterialKind.TMK_NONE;
            case +1 -> TextureMaterialKind.TMK_ALBEDO;
            case +2 -> TextureMaterialKind.TMK_SPECULAR;
            case +3 -> TextureMaterialKind.TMK_NORMAL;
            case +4 -> TextureMaterialKind.TMK_SMOOTHNESS;
            case +5 -> TextureMaterialKind.TMK_COVER;
            case +6 -> TextureMaterialKind.TMK_SSSMASK;
            case +7 -> TextureMaterialKind.TMK_COLORMASK;
            case +8 -> TextureMaterialKind.TMK_BLOOMMASK;
            case +9 -> TextureMaterialKind.TMK_HEIGHTMAP;
            case 10 -> TextureMaterialKind.TMK_DECALALBEDO;
            case 11 -> TextureMaterialKind.TMK_DECALNORMAL;
            case 12 -> TextureMaterialKind.TMK_DECALSPECULAR;
            case 13 -> TextureMaterialKind.TMK_LIGHTPROJECT;
            case 14 -> TextureMaterialKind.TMK_PARTICLE;
            case 15 -> TextureMaterialKind.TMK_DECALHEIGHTMAP;
            case 16 -> TextureMaterialKind.TMK_AO;
            case 18 -> TextureMaterialKind.TMK_UI;
            case 19 -> TextureMaterialKind.TMK_FONT;
            case 20 -> TextureMaterialKind.TMK_LEGACY_FLASH_UI;
            case 22 -> TextureMaterialKind.TMK_BLENDMASK;
            case 23 -> TextureMaterialKind.TMK_PAINTEDDATAGRID;
            default -> throw new IllegalArgumentException("Invalid texture material kind value " + value);
        };
    }

    private static TextureFormat toTextureFormat(int value) {
        return switch (value) {
            case +0 -> TextureFormat.FMT_NONE;
            case +1 -> TextureFormat.FMT_RGBA32F;
            case +2 -> TextureFormat.FMT_RGBA16F;
            case +3 -> TextureFormat.FMT_RGBA8;
            case +4 -> TextureFormat.FMT_ARGB8;
            case +5 -> TextureFormat.FMT_ALPHA;
            case +6 -> TextureFormat.FMT_L8A8_DEPRECATED;
            case +7 -> TextureFormat.FMT_RG8;
            case +8 -> TextureFormat.FMT_LUM8_DEPRECATED;
            case +9 -> TextureFormat.FMT_INT8_DEPRECATED;
            case 10 -> TextureFormat.FMT_BC1;
            case 11 -> TextureFormat.FMT_BC3;
            case 12 -> TextureFormat.FMT_DEPTH;
            case 13 -> TextureFormat.FMT_DEPTH_STENCIL;
            case 14 -> TextureFormat.FMT_X32F;
            case 15 -> TextureFormat.FMT_Y16F_X16F;
            case 16 -> TextureFormat.FMT_X16;
            case 17 -> TextureFormat.FMT_Y16_X16;
            case 18 -> TextureFormat.FMT_RGB565;
            case 19 -> TextureFormat.FMT_R8;
            case 20 -> TextureFormat.FMT_R11FG11FB10F;
            case 21 -> TextureFormat.FMT_X16F;
            case 22 -> TextureFormat.FMT_BC6H_UF16;
            case 23 -> TextureFormat.FMT_BC7;
            case 24 -> TextureFormat.FMT_BC4;
            case 25 -> TextureFormat.FMT_BC5;
            case 26 -> TextureFormat.FMT_RG16F;
            case 27 -> TextureFormat.FMT_R10G10B10A2;
            case 28 -> TextureFormat.FMT_RG32F;
            case 29 -> TextureFormat.FMT_R32_UINT;
            case 30 -> TextureFormat.FMT_R16_UINT;
            case 31 -> TextureFormat.FMT_DEPTH16;
            case 32 -> TextureFormat.FMT_RGBA8_SRGB;
            case 33 -> TextureFormat.FMT_BC1_SRGB;
            case 34 -> TextureFormat.FMT_BC3_SRGB;
            case 35 -> TextureFormat.FMT_BC7_SRGB;
            case 36 -> TextureFormat.FMT_BC6H_SF16;
            case 37 -> TextureFormat.FMT_ASTC_4X4;
            case 38 -> TextureFormat.FMT_ASTC_4X4_SRGB;
            case 39 -> TextureFormat.FMT_ASTC_5X4;
            case 40 -> TextureFormat.FMT_ASTC_5X4_SRGB;
            case 41 -> TextureFormat.FMT_ASTC_5X5;
            case 42 -> TextureFormat.FMT_ASTC_5X5_SRGB;
            case 43 -> TextureFormat.FMT_ASTC_6X5;
            case 44 -> TextureFormat.FMT_ASTC_6X5_SRGB;
            case 45 -> TextureFormat.FMT_ASTC_6X6;
            case 46 -> TextureFormat.FMT_ASTC_6X6_SRGB;
            case 47 -> TextureFormat.FMT_ASTC_8X5;
            case 48 -> TextureFormat.FMT_ASTC_8X5_SRGB;
            case 49 -> TextureFormat.FMT_ASTC_8X6;
            case 50 -> TextureFormat.FMT_ASTC_8X6_SRGB;
            case 51 -> TextureFormat.FMT_ASTC_8X8;
            case 52 -> TextureFormat.FMT_ASTC_8X8_SRGB;
            case 53 -> TextureFormat.FMT_DEPTH32F;
            case 54 -> TextureFormat.FMT_BC1_ZERO_ALPHA;
            case 55 -> TextureFormat.FMT_R8_UINT;
            case 56 -> TextureFormat.FMT_RGBA16_UINT;
            case 57 -> TextureFormat.FMT_R9G9B9E5;
            case 58 -> TextureFormat.FMT_RG32_UINT;
            case 59 -> TextureFormat.FMT_RGBA16;
            case 60 -> TextureFormat.FMT_SMALLF;
            case 61 -> TextureFormat.FMT_MAINVIEW_SMALLF;
            case 62 -> TextureFormat.FMT_RG16_UINT;
            default -> throw new IllegalArgumentException("Invalid texture format value " + value);
        };
    }
}
