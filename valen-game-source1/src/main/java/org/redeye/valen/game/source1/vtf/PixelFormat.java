package org.redeye.valen.game.source1.vtf;

import be.twofold.valen.core.texture.*;

public enum PixelFormat {
    NONE,
    RGBA8888,
    ABGR8888,
    RGB888,
    BGR888,
    RGB565,
    I8,
    IA88,
    P8,
    A8,
    RGB888_BLUESCREEN,
    BGR888_BLUESCREEN,
    ARGB8888,
    BGRA8888,
    DXT1,
    DXT3,
    DXT5,
    BGRX8888,
    BGR565,
    BGRX5551,
    BGRA4444,
    DXT1_ONEBITALPHA,
    BGRA5551,
    UV88,
    UVWQ8888,
    RGBA16161616F,
    RGBA16161616,
    UVLX8888;

    public TextureFormat toTextureFormat() {
        return switch (this) {
            case DXT1 -> TextureFormat.BC1_UNORM;
            case DXT3 -> TextureFormat.BC2_UNORM;
            case DXT5 -> TextureFormat.BC3_UNORM;
            case RGBA8888 -> TextureFormat.R8G8B8A8_UNORM;
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
