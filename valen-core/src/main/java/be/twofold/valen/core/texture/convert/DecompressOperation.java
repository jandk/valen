package be.twofold.valen.core.texture.convert;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;

public final class DecompressOperation implements Operation {
    public DecompressOperation() {
    }

    @Override
    public Surface apply(Surface surface) {
        if (!surface.format().isCompressed()) {
            return surface;
        }

        var format = getTextureFormat(surface.format());
        var decoder = BlockDecoder.create(getBlockFormat(surface.format()), getOrder(format));

        var result = Surface.create(surface.width(), surface.height(), format);
        decoder.decode(surface.width(), surface.height(), surface.data(), 0, result.data(), 0);
        return result;
    }

    @Override
    public Texture map(Texture source, TextureFormat ignored) {
        if (!source.format().isCompressed()) {
            return source;
        }

        var format = getTextureFormat(source.format());
        var decoder = BlockDecoder.create(getBlockFormat(source.format()), getOrder(format));

        var surfaces = source.surfaces().stream()
            .map(surface -> {
                var result = Surface.create(surface.width(), surface.height(), format);
                decoder.decode(surface.width(), surface.height(), surface.data(), 0, result.data(), 0);
                return result;
            })
            .toList();

        return source
            .withFormat(format)
            .withSurfaces(surfaces);
    }

    private static TextureFormat getTextureFormat(TextureFormat source) {
        return switch (source) {
            case BC1_SRGB, BC1_UNORM,
                 BC2_SRGB, BC2_UNORM,
                 BC3_SRGB, BC3_UNORM,
                 BC7_SRGB, BC7_UNORM -> TextureFormat.R8G8B8A8_UNORM;
            case BC4_SNORM, BC4_UNORM -> TextureFormat.R8_UNORM;
            case BC5_SNORM, BC5_UNORM -> TextureFormat.R8G8B8_UNORM;
            case BC6H_SFLOAT, BC6H_UFLOAT -> TextureFormat.R16G16B16A16_SFLOAT;
            default -> throw Operation.uoe(source);
        };
    }

    private static BlockFormat getBlockFormat(TextureFormat format) {
        return switch (format) {
            case BC1_SRGB, BC1_UNORM -> BlockFormat.BC1;
            case BC2_SRGB, BC2_UNORM -> BlockFormat.BC2;
            case BC3_SRGB, BC3_UNORM -> BlockFormat.BC3;
            case BC4_SNORM -> BlockFormat.BC4Signed;
            case BC4_UNORM -> BlockFormat.BC4Unsigned;
            case BC5_SNORM -> BlockFormat.BC5SignedNormalized;
            case BC5_UNORM -> BlockFormat.BC5UnsignedNormalized;
            case BC6H_SFLOAT -> BlockFormat.BC6Signed;
            case BC6H_UFLOAT -> BlockFormat.BC6Unsigned;
            case BC7_SRGB, BC7_UNORM -> BlockFormat.BC7;
            default -> throw Operation.uoe(format);
        };
    }

    private static PixelOrder getOrder(TextureFormat format) {
        return switch (format) {
            case R8_UNORM -> PixelOrder.R;
            case R8G8B8_UNORM -> PixelOrder.RGB;
            case R8G8B8A8_UNORM, R16G16B16A16_SFLOAT -> PixelOrder.RGBA;
            default -> throw Operation.uoe(format);
        };
    }
}
