package be.twofold.valen.core.texture;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.ByteArrays;

import java.util.function.*;

final class TextureConverter {
    private TextureConverter() {
    }

    static Texture convert(Texture source, TextureFormat targetFormat) {
        if (source.format() == targetFormat) {
            return source;
        }

        if (targetFormat.isCompressed()) {
            throw new UnsupportedOperationException("Compressing textures is not supported");
        }

        source = decompress(source);
        source = tonemap(source);
        source = unpack(source, targetFormat);
        source = swizzle(source, targetFormat);
        return source;
    }

    private static Texture map(Texture source, TextureFormat format, Function<Surface, Surface> surfaceMapper) {
        var surfaces = source.surfaces().stream()
            .map(surfaceMapper)
            .toList();

        return source
            .withFormat(format)
            .withSurfaces(surfaces);
    }

    private static RuntimeException uoe(TextureFormat format) {
        return new UnsupportedOperationException("Unsupported texture format: " + format);
    }

    // region Decompress

    private static Texture decompress(Texture source) {
        if (!source.format().isCompressed()) {
            return source;
        }

        var format = getTextureFormat(source.format());
        var operator = decompressOperator(source, format);
        return map(source, format, operator);
    }

    private static UnaryOperator<Surface> decompressOperator(Texture source, TextureFormat format) {
        var decoder = BlockDecoder.create(getBlockFormat(source.format()), getOrder(format));
        return surface -> {
            var result = Surface.create(surface.width(), surface.height(), format);
            decoder.decode(surface.width(), surface.height(), surface.data(), 0, result.data(), 0);
            return result;
        };
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
            default -> throw uoe(source);
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
            default -> throw uoe(format);
        };
    }

    private static PixelOrder getOrder(TextureFormat format) {
        return switch (format) {
            case R8_UNORM -> PixelOrder.R;
            case R8G8B8_UNORM -> PixelOrder.RGB;
            case R8G8B8A8_UNORM, R16G16B16A16_SFLOAT -> PixelOrder.RGBA;
            default -> throw uoe(format);
        };
    }

    // endregion

    // region Tonemap

    private static Texture tonemap(Texture texture) {
        var operatorFormat = tonemapOperator(texture.format());
        if (operatorFormat == null) {
            return texture;
        }

        return map(texture, operatorFormat.format(), operatorFormat.operator());
    }

    private static OperatorFormat tonemapOperator(TextureFormat format) {
        return switch (format) {
            case R16_UNORM ->
                new OperatorFormat(surface -> tonemapU16(surface, TextureFormat.R8_UNORM), TextureFormat.R8_UNORM);
            case R16G16B16A16_UNORM ->
                new OperatorFormat(surface -> tonemapU16(surface, TextureFormat.R8G8B8A8_UNORM), TextureFormat.R8G8B8A8_UNORM);
            case R16_SFLOAT ->
                new OperatorFormat(surface -> tonemapF16(surface, TextureFormat.R8_UNORM), TextureFormat.R8_UNORM);
            case R16G16_SFLOAT ->
                new OperatorFormat(surface -> tonemapF16(surface, TextureFormat.R8G8_UNORM), TextureFormat.R8G8_UNORM);
            case R16G16B16A16_SFLOAT ->
                new OperatorFormat(surface -> tonemapF16(surface, TextureFormat.R8G8B8A8_UNORM), TextureFormat.R8G8B8A8_UNORM);
            default -> null;
        };
    }

    private static Surface tonemapU16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = target.data();
        for (int i = 1, o = 0; i < src.length; i += 2, o++) {
            dst[o] = src[i];
        }
        return target;
    }

    private static Surface tonemapF16(Surface surface, TextureFormat format) {
        Surface target = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = target.data();
        for (int i = 0, o = 0; i < src.length; i += 2, o++) {
            dst[o] = MathF.packUNorm8(Float.float16ToFloat(ByteArrays.getShort(src, i)));
        }
        return target;
    }

    // endregion

    // region Unpack

    private static Texture unpack(Texture texture, TextureFormat format) {
        if (texture.format() == format) {
            return texture;
        }

        var operatorFormat = unpackOperator(texture.format(), format);
        if (operatorFormat == null) {
            return texture;
        }
        return map(texture, operatorFormat.format(), operatorFormat.operator());
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    private static OperatorFormat unpackOperator(TextureFormat srcFormat, TextureFormat dstFormat) {
        switch (srcFormat) {
            case R8_UNORM -> {
                switch (dstFormat) {
                    case R8G8_UNORM -> {
                        return unpackOperator(TextureFormat.R8G8_UNORM, 1, new byte[]{0x00});
                    }
                    case R8G8B8_UNORM, B8G8R8_UNORM -> {
                        return unpackOperator(TextureFormat.R8G8B8_UNORM, 1, new byte[]{0x00, 0x00});
                    }
                    case R8G8B8A8_UNORM, B8G8R8A8_UNORM -> {
                        return unpackOperator(TextureFormat.R8G8B8A8_UNORM, 1, new byte[]{0x00, 0x00, (byte) 0xFF});
                    }
                }
            }
            case R8G8_UNORM -> {
                switch (dstFormat) {
                    case R8G8B8_UNORM, B8G8R8_UNORM -> {
                        return unpackOperator(TextureFormat.R8G8B8_UNORM, 2, new byte[]{0x00});
                    }
                    case R8G8B8A8_UNORM, B8G8R8A8_UNORM -> {
                        return unpackOperator(TextureFormat.R8G8B8A8_UNORM, 2, new byte[]{0x00, (byte) 0xFF});
                    }
                }
            }
            case R8G8B8_UNORM -> {
                switch (dstFormat) {
                    case R8G8B8A8_UNORM, B8G8R8A8_UNORM -> {
                        return unpackOperator(TextureFormat.R8G8B8A8_UNORM, 3, new byte[]{(byte) 0xFF});
                    }
                }
            }
            case B8G8R8_UNORM -> {
                switch (dstFormat) {
                    case R8G8B8A8_UNORM, B8G8R8A8_UNORM -> {
                        return unpackOperator(TextureFormat.B8G8R8A8_UNORM, 3, new byte[]{(byte) 0xFF});
                    }
                }
            }
            case R16_UNORM -> {
                switch (dstFormat) {
                    case R16G16B16A16_UNORM -> {
                        return unpackOperator(TextureFormat.R16G16B16A16_UNORM, 2, new byte[]{0x00, 0x00, 0x00, 0x00, (byte) 0xFF, (byte) 0xFF});
                    }
                }
            }
            case R16_SFLOAT -> {
                switch (dstFormat) {
                    case R16G16_SFLOAT -> {
                        return unpackOperator(TextureFormat.R16G16_SFLOAT, 2, new byte[]{0x00, 0x00});
                    }
                    case R16G16B16A16_SFLOAT -> {
                        return unpackOperator(TextureFormat.R16G16B16A16_SFLOAT, 2, new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x3C});
                    }
                }
            }
            case R16G16_SFLOAT -> {
                switch (dstFormat) {
                    case R16G16B16A16_SFLOAT -> {
                        return unpackOperator(TextureFormat.R16G16B16A16_SFLOAT, 4, new byte[]{0x00, 0x00, 0x00, 0x3C});
                    }
                }
            }
        }
        return null;
    }

    private static OperatorFormat unpackOperator(TextureFormat format, int stride, byte[] filler) {
        return new OperatorFormat(surface -> unpack(surface, format, stride, filler), format);
    }

    private static Surface unpack(Surface surface, TextureFormat format, int stride, byte[] filler) {
        var result = Surface.create(surface.width(), surface.height(), format);

        var src = surface.data();
        var dst = result.data();

        for (int i = 0, o = 0; i < src.length; i += stride) {
            System.arraycopy(src, i, dst, o, stride);
            o += stride;
            System.arraycopy(filler, 0, dst, o, filler.length);
            o += filler.length;
        }

        return result;
    }

    // endregion

    // region Swizzle

    private static Texture swizzle(Texture texture, TextureFormat format) {
        if (texture.format() == format) {
            return texture;
        }

        var operatorFormat = swizzleOperator(texture.format(), format);
        if (operatorFormat == null) {
            return texture;
        }

        return map(texture, operatorFormat.format(), operatorFormat.operator());
    }

    private static OperatorFormat swizzleOperator(TextureFormat srcFormat, TextureFormat dstFormat) {
        if (srcFormat == TextureFormat.R8G8B8_UNORM) {
            if (dstFormat == TextureFormat.B8G8R8_UNORM) {
                return new OperatorFormat(surface -> rgba_bgra(surface, 3), dstFormat);
            }
        } else if (srcFormat == TextureFormat.R8G8B8A8_UNORM) {
            if (dstFormat == TextureFormat.B8G8R8A8_UNORM) {
                return new OperatorFormat(surface -> rgba_bgra(surface, 4), dstFormat);
            }
        } else if (srcFormat == TextureFormat.B8G8R8_UNORM) {
            if (dstFormat == TextureFormat.R8G8B8_UNORM) {
                return new OperatorFormat(surface -> rgba_bgra(surface, 3), dstFormat);
            }
        } else if (srcFormat == TextureFormat.B8G8R8A8_UNORM) {
            if (dstFormat == TextureFormat.R8G8B8A8_UNORM) {
                return new OperatorFormat(surface -> rgba_bgra(surface, 4), dstFormat);
            }
        }
        return null;
    }

    private static Surface rgba_bgra(Surface surface, int stride) {
        var data = surface.data();
        for (int i = 0; i < data.length; i += stride) {
            swap(data, i, i + 2);
        }
        return surface;
    }

    private static void swap(byte[] array, int i, int j) {
        byte tmp = array[i];
        array[i] = array[j];
        array[j] = tmp;
    }

    // endregion

    private record OperatorFormat(
        UnaryOperator<Surface> operator,
        TextureFormat format
    ) {
    }
}
