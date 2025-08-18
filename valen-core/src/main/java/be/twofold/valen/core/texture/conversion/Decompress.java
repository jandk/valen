package be.twofold.valen.core.texture.conversion;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;

final class Decompress extends Conversion {
    private final boolean reconstructZ;

    Decompress(boolean reconstructZ) {
        this.reconstructZ = reconstructZ;
    }

    @Override
    Surface apply(Surface surface, TextureFormat dstFormat) {
        if (!surface.format().isCompressed()) {
            return surface;
        }

        var format = getTextureFormat(surface.format());
        var decoder = BlockDecoder.create(getBlockFormat(surface.format()));

        var result = Surface.create(surface.width(), surface.height(), format);
        decoder.decode(surface.width(), surface.height(), surface.data(), 0, result.data(), 0);
        return result;
    }

    private TextureFormat getTextureFormat(TextureFormat source) {
        return switch (source) {
            case BC1_SRGB, BC1_UNORM,
                 BC2_SRGB, BC2_UNORM,
                 BC3_SRGB, BC3_UNORM,
                 BC7_SRGB, BC7_UNORM -> TextureFormat.R8G8B8A8_UNORM;
            case BC4_SNORM, BC4_UNORM -> TextureFormat.R8_UNORM;
            case BC5_SNORM, BC5_UNORM -> TextureFormat.R8G8B8_UNORM;
            case BC6H_SFLOAT, BC6H_UFLOAT -> TextureFormat.R16G16B16_SFLOAT;
            default -> throw uoe(source);
        };
    }

    private BlockFormat getBlockFormat(TextureFormat format) {
        return switch (format) {
            case BC1_SRGB, BC1_UNORM -> BlockFormat.BC1;
            case BC2_SRGB, BC2_UNORM -> BlockFormat.BC2;
            case BC3_SRGB, BC3_UNORM -> BlockFormat.BC3;
            case BC4_SNORM -> BlockFormat.BC4S;
            case BC4_UNORM -> BlockFormat.BC4U;
            case BC5_SNORM -> reconstructZ ? BlockFormat.BC5S_RECONSTRUCT_Z : BlockFormat.BC5S;
            case BC5_UNORM -> reconstructZ ? BlockFormat.BC5U_RECONSTRUCT_Z : BlockFormat.BC5U;
            case BC6H_SFLOAT -> BlockFormat.BC6H_SF16;
            case BC6H_UFLOAT -> BlockFormat.BC6H_UF16;
            case BC7_SRGB, BC7_UNORM -> BlockFormat.BC7;
            default -> throw uoe(format);
        };
    }
}
