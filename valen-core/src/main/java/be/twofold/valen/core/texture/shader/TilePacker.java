package be.twofold.valen.core.texture.shader;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;

@FunctionalInterface
interface TilePacker {
    void pack(float[] src, int tileW, int tileH, int dstX, int dstY, int dstZ);

    static TilePacker forSurface(Surface dst) {
        if (dst.format().isCompressed()) {
            throw new UnsupportedOperationException("Cannot pack to compressed format: " + dst.format());
        }
        return switch (dst.format()) {
            case R8_UNORM, R8_SRGB ->
                (src, tileW, tileH, dstX, dstY, dstZ) -> packR8(src, tileW, tileH, dst, dstX, dstY, dstZ);
            case R8G8_UNORM ->
                (src, tileW, tileH, dstX, dstY, dstZ) -> packR8G8(src, tileW, tileH, dst, dstX, dstY, dstZ);
            case R8G8B8_UNORM, R8G8B8_SRGB ->
                (src, tileW, tileH, dstX, dstY, dstZ) -> packR8G8B8(src, tileW, tileH, dst, dstX, dstY, dstZ);
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB ->
                (src, tileW, tileH, dstX, dstY, dstZ) -> packR8G8B8A8(src, tileW, tileH, dst, dstX, dstY, dstZ);
            case B8G8R8_UNORM, B8G8R8_SRGB ->
                (src, tileW, tileH, dstX, dstY, dstZ) -> packB8G8R8(src, tileW, tileH, dst, dstX, dstY, dstZ);
            case B8G8R8A8_UNORM, B8G8R8A8_SRGB ->
                (src, tileW, tileH, dstX, dstY, dstZ) -> packB8G8R8A8(src, tileW, tileH, dst, dstX, dstY, dstZ);
            case R16G16B16A16_SFLOAT ->
                (src, tileW, tileH, dstX, dstY, dstZ) -> packR16G16B16A16Sfloat(src, tileW, tileH, dst, dstX, dstY, dstZ);
            default -> throw new UnsupportedOperationException("No packer for: " + dst.format());
        };
    }

    private static void packR8(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff++) {
                data.set(dstOff, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff]) : src[srcOff]));
            }
        }
    }

    private static void packR8G8(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, dstOff += 2, srcOff += 4) {
                data.set(dstOff/**/, FloatMath.packUNorm8(src[srcOff/**/]));
                data.set(dstOff + 1, FloatMath.packUNorm8(src[srcOff + 1]));
            }
        }
    }

    private static void packR8G8B8(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 3) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/]));
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1]));
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2]));
            }
        }
    }

    private static void packR8G8B8A8(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 4) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/]));
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1]));
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2]));
                data.set(dstOff + 3, FloatMath.packUNorm8(src[srcOff + 3]));
            }
        }
    }

    private static void packB8G8R8(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 3) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2]));
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1]));
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/]));
            }
        }
    }

    private static void packB8G8R8A8(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 4) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2]));
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1]));
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/]));
                data.set(dstOff + 3, FloatMath.packUNorm8(src[srcOff + 3]));
            }
        }
    }

    private static void packR16G16B16A16Sfloat(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 8) {
                data.setShort(dstOff/**/, Float.floatToFloat16(src[srcOff/**/]));
                data.setShort(dstOff + 2, Float.floatToFloat16(src[srcOff + 1]));
                data.setShort(dstOff + 4, Float.floatToFloat16(src[srcOff + 2]));
                data.setShort(dstOff + 6, Float.floatToFloat16(src[srcOff + 3]));
            }
        }
    }

    private static Bytes.Mutable dataFrom(Surface surface) {
        return (Bytes.Mutable) surface.data();
    }
}
