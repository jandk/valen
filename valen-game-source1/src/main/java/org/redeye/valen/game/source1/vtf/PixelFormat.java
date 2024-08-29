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
        switch (this) {
            case DXT1 -> {
                return TextureFormat.Bc1UNorm;
            }
            case DXT3 -> {
                return TextureFormat.Bc2UNorm;
            }
            case DXT5 -> {
                return TextureFormat.Bc3UNorm;
            }
            case RGBA8888 -> {
                return TextureFormat.R8G8B8A8UNorm;
            }
            default -> {
                throw new RuntimeException("VTF texture format not supported: " + this.name());
            }
        }
    }

    public boolean isBlockCompressed() {
        switch (this) {
            case DXT1, DXT3, DXT5, DXT1_ONEBITALPHA:
                return true;
            default:
                return false;
        }
    }

    public int blockSize() {
        switch (this) {
            case DXT1, DXT1_ONEBITALPHA -> {
                return 8;
            }
            case DXT3, DXT5 -> {
                return 16;
            }
            default -> {
                return 0;
            }
        }
    }
}
