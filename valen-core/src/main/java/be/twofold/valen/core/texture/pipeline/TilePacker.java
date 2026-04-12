package be.twofold.valen.core.texture.pipeline;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;

@FunctionalInterface
interface TilePacker {
    void pack(float[] src, int tw, int th, int dx, int dy);

    static TilePacker forSurface(Surface dst) {
        if (dst.format().isCompressed()) {
            throw new UnsupportedOperationException("Cannot pack to compressed format: " + dst.format());
        }
        Bytes.Mutable data = dst.mutableData();
        return switch (dst.format()) {
            case R8_UNORM, R8_SRGB -> (src, tw, th, dx, dy) -> packR8(src, tw, th, dst, dx, dy);
            case R8G8_UNORM -> (src, tw, th, dx, dy) -> packR8G8(src, tw, th, dst, dx, dy);
            case R8G8B8_UNORM, R8G8B8_SRGB -> (src, tw, th, dx, dy) -> packR8G8B8(src, tw, th, dst, dx, dy);
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB -> (src, tw, th, dx, dy) -> packR8G8B8A8(src, tw, th, dst, dx, dy);
            case B8G8R8_UNORM, B8G8R8_SRGB -> (src, tw, th, dx, dy) -> packB8G8R8(src, tw, th, dst, dx, dy);
            case B8G8R8A8_UNORM, B8G8R8A8_SRGB -> (src, tw, th, dx, dy) -> packB8G8R8A8(src, tw, th, dst, dx, dy);
            case R16G16B16A16_SFLOAT -> (src, tw, th, dx, dy) -> packR16G16B16A16Sfloat(src, tw, th, dst, dx, dy);
            default -> throw new UnsupportedOperationException("No packer for: " + dst.format());
        };
    }

    private static void packR8(float[] src, int tw, int th, Surface dst, int dstX, int dstY) {
        Bytes.Mutable data = dst.mutableData();
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < th; row++) {
            int srcOff = row * tw * 4;
            int dstOff = (dstY + row) * dst.width() + dstX;
            for (int col = 0; col < tw; col++, srcOff += 4, dstOff++) {
                data.set(dstOff, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff]) : src[srcOff]));
            }
        }
    }

    private static void packR8G8(float[] src, int tw, int th, Surface dst, int dstX, int dstY) {
        Bytes.Mutable data = dst.mutableData();
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < th; row++) {
            int srcOff = row * tw * 4;
            int dstOff = ((dstY + row) * dst.width() + dstX) * 2;
            for (int col = 0; col < tw; col++, dstOff += 2, srcOff += 4) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/]));
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1]));
            }
        }
    }

    private static void packR8G8B8(float[] src, int tw, int th, Surface dst, int dx, int dy) {
        Bytes.Mutable data = dst.mutableData();
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < th; row++) {
            int srcOff = row * tw * 4;
            int dstOff = ((dy + row) * dst.width() + dx) * 3;
            for (int col = 0; col < tw; col++, srcOff += 4, dstOff += 3) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/]));
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1]));
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2]));
            }
        }
    }

    private static void packR8G8B8A8(float[] src, int tw, int th, Surface dst, int dx, int dy) {
        Bytes.Mutable data = dst.mutableData();
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < th; row++) {
            int srcOff = row * tw * 4;
            int dstOff = ((dy + row) * dst.width() + dx) * 4;
            for (int col = 0; col < tw; col++, srcOff += 4, dstOff += 4) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/]));
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1]));
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2]));
                data.set(dstOff + 3, FloatMath.packUNorm8(src[srcOff + 3])); // alpha is always linear
            }
        }
    }

    private static void packB8G8R8(float[] src, int tw, int th, Surface dst, int dx, int dy) {
        Bytes.Mutable data = dst.mutableData();
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < th; row++) {
            int srcOff = row * tw * 4;
            int dstOff = ((dy + row) * dst.width() + dx) * 3;
            for (int col = 0; col < tw; col++, srcOff += 4, dstOff += 3) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2])); // B
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1])); // G
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/])); // R
            }
        }
    }

    private static void packB8G8R8A8(float[] src, int tw, int th, Surface dst, int dx, int dy) {
        Bytes.Mutable data = dst.mutableData();
        boolean srgb = dst.format().isSrgb();
        for (int row = 0; row < th; row++) {
            int srcOff = row * tw * 4;
            int dstOff = ((dy + row) * dst.width() + dx) * 4;
            for (int col = 0; col < tw; col++, srcOff += 4, dstOff += 4) {
                data.set(dstOff/**/, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 2]) : src[srcOff + 2])); // B
                data.set(dstOff + 1, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff + 1]) : src[srcOff + 1])); // G
                data.set(dstOff + 2, FloatMath.packUNorm8(srgb ? MathF.linearToSrgb(src[srcOff/**/]) : src[srcOff/**/])); // R
                data.set(dstOff + 3, FloatMath.packUNorm8(src[srcOff + 3])); // alpha is always linear
            }
        }
    }

    private static void packR16G16B16A16Sfloat(float[] src, int tw, int th, Surface dst, int dx, int dy) {
        Bytes.Mutable data = dst.mutableData();
        for (int row = 0; row < th; row++) {
            int srcOff = row * tw * 4;
            int dstOff = ((dy + row) * dst.width() + dx) * 8;
            for (int col = 0; col < tw; col++, srcOff += 4, dstOff += 8) {
                data.setShort(dstOff/**/, Float.floatToFloat16(src[srcOff/**/]));
                data.setShort(dstOff + 2, Float.floatToFloat16(src[srcOff + 1]));
                data.setShort(dstOff + 4, Float.floatToFloat16(src[srcOff + 2]));
                data.setShort(dstOff + 6, Float.floatToFloat16(src[srcOff + 3]));
            }
        }
    }
}
