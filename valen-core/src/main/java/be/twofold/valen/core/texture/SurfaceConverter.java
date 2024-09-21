package be.twofold.valen.core.texture;

import be.twofold.tinybcdec.*;

public final class SurfaceConverter {
    private SurfaceConverter() {
    }

    public static Surface convert(Surface source, TextureFormat format) {
        if (source.format() == format) {
            return source;
        }
        if (format.isCompressed()) {
            throw uoe(source.format(), format);
        }

        var target = Surface.create(source.width(), source.height(), format);
        if (source.format().isCompressed()) {
            decompress(source, target);
        }
        return target;
    }

    private static void decompress(Surface source, Surface target) {
        if (target.format().isCompressed()) {
            throw uoe(source.format(), target.format());
        }

        var pixelOrder = switch (target.format().order().orElseThrow()) {
            case R -> PixelOrder.R;
            case RG -> PixelOrder.of(2, 0, 1, -1, -1);
            case RGB -> PixelOrder.RGB;
            case RGBA -> PixelOrder.RGBA;
            case BGRA -> PixelOrder.BGRA;
            default -> throw new UnsupportedOperationException("Unsupported order: " + target.format().order());
        };

        var blockFormat = switch (source.format().block()) {
            case BC1 -> BlockFormat.BC1;
            case BC2 -> BlockFormat.BC2;
            case BC3 -> BlockFormat.BC3;
            case BC4 -> source.format() == TextureFormat.BC4_UNORM
                ? BlockFormat.BC4Unsigned
                : BlockFormat.BC4Signed;
            case BC5 -> source.format() == TextureFormat.BC5_UNORM
                ? BlockFormat.BC5UnsignedNormalized
                : BlockFormat.BC5SignedNormalized;
            case BC7 -> BlockFormat.BC7;
            default -> throw new UnsupportedOperationException("Unsupported block: " + source.format().block());
        };

        BlockDecoder
            .create(blockFormat, pixelOrder)
            .decode(target.width(), target.height(), source.data(), 0, target.data(), 0);
    }

    private static UnsupportedOperationException uoe(TextureFormat sourceFormat, TextureFormat targetFormat) {
        return new UnsupportedOperationException("Unsupported conversion: " + sourceFormat + " -> " + targetFormat);
    }
}
