package be.twofold.valen.core.texture.conversion;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;

final class Decompress extends Conversion {
    Decompress() {
    }

    @Override
    Surface apply(Surface surface, TextureFormat dstFormat) {
        if (!surface.format().isCompressed()) {
            return surface;
        }

        var format = getTextureFormat(surface.format());
        var decoder = getBlockDecoder(surface.format());

        var result = Surface.create(surface.width(), surface.height(), format);
        decoder.decode(surface.data(), 0, surface.width(), surface.height(), result.data(), 0);
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

    private BlockDecoder getBlockDecoder(TextureFormat format) {
        return switch (format) {
            case BC1_SRGB, BC1_UNORM -> BlockDecoder.bc1(false);
            case BC2_SRGB, BC2_UNORM -> BlockDecoder.bc2();
            case BC3_SRGB, BC3_UNORM -> BlockDecoder.bc3();
            case BC4_SNORM -> BlockDecoder.bc4(true);
            case BC4_UNORM -> BlockDecoder.bc4(false);
            case BC5_SNORM -> BlockDecoder.bc5(true);
            case BC5_UNORM -> BlockDecoder.bc5(false);
            case BC6H_SFLOAT -> BlockDecoder.bc6h(true);
            case BC6H_UFLOAT -> BlockDecoder.bc6h(false);
            case BC7_SRGB, BC7_UNORM -> BlockDecoder.bc7();
            default -> throw uoe(format);
        };
    }
}
