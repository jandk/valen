package be.twofold.valen.core.texture.shader;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;

@FunctionalInterface
interface TilePacker {
    void pack(Context ctx, float[] src);

    static TilePacker forSurface(Surface dst) {
        if (dst.format().isCompressed()) {
            throw new UnsupportedOperationException("Cannot pack to compressed format: " + dst.format());
        }
        return switch (dst.format()) {
            case R8_UNORM, R8_SRGB -> (ctx, src) -> packR8(src, ctx.width, ctx.height, dst, ctx.x, ctx.y, ctx.z);
            case R8G8_UNORM -> (ctx, src) -> packR8G8(src, ctx.width, ctx.height, dst, ctx.x, ctx.y, ctx.z);
            case R8G8B8_UNORM, R8G8B8_SRGB ->
                (ctx, src) -> packR8G8B8(src, ctx.width, ctx.height, dst, ctx.x, ctx.y, ctx.z);
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB ->
                (ctx, src) -> packR8G8B8A8(src, ctx.width, ctx.height, dst, ctx.x, ctx.y, ctx.z);
            case B8G8R8_UNORM, B8G8R8_SRGB ->
                (ctx, src) -> packB8G8R8(src, ctx.width, ctx.height, dst, ctx.x, ctx.y, ctx.z);
            case B8G8R8A8_UNORM, B8G8R8A8_SRGB ->
                (ctx, src) -> packB8G8R8A8(src, ctx.width, ctx.height, dst, ctx.x, ctx.y, ctx.z);
            case R16G16B16A16_SFLOAT ->
                (ctx, src) -> packR16G16B16A16Sfloat(src, ctx.width, ctx.height, dst, ctx.x, ctx.y, ctx.z);
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
                data.set(dstOff, encode(src[srcOff], srgb));
            }
        }
    }

    private static void packR8G8(float[] src, int tileW, int tileH, Surface dst, int dstX, int dstY, int dstZ) {
        Bytes.Mutable data = dataFrom(dst);
        for (int row = 0; row < tileH; row++) {
            int srcOff = row * tileW * 4;
            int dstOff = dst.offset(dstX, dstY + row, dstZ);
            for (int col = 0; col < tileW; col++, dstOff += 2, srcOff += 4) {
                data.set(dstOff/**/, encode(src[srcOff/**/], false));
                data.set(dstOff + 1, encode(src[srcOff + 1], false));
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
                data.set(dstOff/**/, encode(src[srcOff/**/], srgb));
                data.set(dstOff + 1, encode(src[srcOff + 1], srgb));
                data.set(dstOff + 2, encode(src[srcOff + 2], srgb));
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
                data.set(dstOff/**/, encode(src[srcOff/**/], srgb));
                data.set(dstOff + 1, encode(src[srcOff + 1], srgb));
                data.set(dstOff + 2, encode(src[srcOff + 2], srgb));
                data.set(dstOff + 3, encode(src[srcOff + 3], false)); // alpha is always linear
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
                data.set(dstOff/**/, encode(src[srcOff + 2], srgb));
                data.set(dstOff + 1, encode(src[srcOff + 1], srgb));
                data.set(dstOff + 2, encode(src[srcOff/**/], srgb));
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
                data.set(dstOff/**/, encode(src[srcOff + 2], srgb));
                data.set(dstOff + 1, encode(src[srcOff + 1], srgb));
                data.set(dstOff + 2, encode(src[srcOff/**/], srgb));
                data.set(dstOff + 3, encode(src[srcOff + 3], false)); // alpha is always linear
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

    private static byte encode(float f, boolean srgb) {
        return srgb ? Srgb.linearToSrgbByte(f) : FloatMath.packUNorm8(f);
    }
}
