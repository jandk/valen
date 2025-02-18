package org.redeye.valen.game.source1.vtf;

import be.twofold.valen.core.texture.*;

public enum ImageFormat {
    RGBA8888(0),
    ABGR8888(1),
    RGB888(2),
    BGR888(3),
    RGB565(4),
    I8(5),
    IA88(6),
    P8(7),
    A8(8),
    RGB888_BLUESCREEN(9),
    BGR888_BLUESCREEN(10),
    ARGB8888(11),
    BGRA8888(12),
    DXT1(13),
    DXT3(14),
    DXT5(15),
    BGRX8888(16),
    BGR565(17),
    BGRX5551(18),
    BGRA4444(19),
    DXT1_ONEBITALPHA(20),
    BGRA5551(21),
    UV88(22),
    UVWQ8888(23),
    RGBA16161616F(24),
    RGBA16161616(25),
    UVLX8888(26),
    R32F(27),            // Single-channel 32-bit floating point
    RGB323232F(28),
    RGBA32323232F(29),
    ;

    private static final ImageFormat[] VALUES = values();
    private final int value;

    ImageFormat(int value) {
        this.value = value;
    }

    public static ImageFormat fromValue(int value) {
        for (var format : VALUES) {
            if (format.value == value) {
                return format;
            }
        }
        throw new UnsupportedOperationException("Unknown image format: " + value);
    }

    public TextureFormat toTextureFormat() {
        return switch (this) {
            case RGBA8888, UVWQ8888, UVLX8888 -> TextureFormat.R8G8B8A8_UNORM;
            case RGB888, RGB888_BLUESCREEN -> TextureFormat.R8G8B8_UNORM;
            case BGR888, BGR888_BLUESCREEN -> TextureFormat.B8G8R8_UNORM;
            case I8, A8 -> TextureFormat.R8_UNORM;
            case BGRA8888, BGRX8888 -> TextureFormat.B8G8R8A8_UNORM;
            case DXT1, DXT1_ONEBITALPHA -> TextureFormat.BC1_UNORM;
            case DXT3 -> TextureFormat.BC2_UNORM;
            case DXT5 -> TextureFormat.BC3_UNORM;
            case UV88 -> TextureFormat.R8G8_UNORM;
            case RGBA16161616F -> TextureFormat.R16G16B16A16_SFLOAT;
            case RGBA16161616 -> TextureFormat.R16G16B16A16_UNORM;
            default -> throw new UnsupportedOperationException("VTF texture format not supported: " + name());
        };
    }

    public boolean isBlockCompressed() {
        return switch (this) {
            case DXT1, DXT3, DXT5, DXT1_ONEBITALPHA -> true;
            default -> false;
        };
    }

    public int blockSize() {
        return switch (this) {
            case DXT1, DXT1_ONEBITALPHA -> 8;
            case DXT3, DXT5 -> 16;
            default -> 0;
        };
    }
}
