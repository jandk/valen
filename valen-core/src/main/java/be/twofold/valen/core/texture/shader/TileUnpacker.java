package be.twofold.valen.core.texture.shader;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;

@FunctionalInterface
interface TileUnpacker {
    void unpack(int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH);

    static TileUnpacker forSurface(Surface source) {
        // TODO: Replace this with tile decompression (update tinybcdec to support it)
        Surface dec = source.format().isCompressed()
            ? decompress(source)
            : source;

        return switch (dec.format()) {
            case R8_UNORM, R8_SRGB ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR8(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R8G8_UNORM ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR8G8(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R8G8B8_UNORM, R8G8B8_SRGB ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR8G8B8(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR8G8B8A8(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case B8G8R8_UNORM, B8G8R8_SRGB ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackB8G8R8(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case B8G8R8A8_UNORM, B8G8R8A8_SRGB ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackB8G8R8A8(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R16_UNORM ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR16Unorm(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R16G16B16A16_UNORM ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR16G16B16A16Unorm(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R16_SFLOAT ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR16Sfloat(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R16G16_SFLOAT ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR16G16Sfloat(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R16G16B16_SFLOAT ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR16G16B16Sfloat(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            case R16G16B16A16_SFLOAT ->
                (srcX, srcY, srcZ, dst, tileW, tileH) -> unpackR16G16B16A16Sfloat(dec, srcX, srcY, srcZ, dst, tileW, tileH);
            default -> throw new UnsupportedOperationException("No unpacker for: " + dec.format());
        };
    }

    // Decompress the entire surface into an intermediate uncompressed format,
    // then delegate to the byte-based unpacker.
    // TODO: replace with tile-level BC decoding once tinybcdec supports it.
    private static Surface decompress(Surface source) {
        BlockDecoder decoder = decoderFor(source.format());
        TextureFormat format = formatFor(source.format());
        byte[] data = new byte[source.width() * source.height() * format.blockSize()];
        decoder.decode(source.data().toArray(), 0, source.width(), source.height(), data, 0);
        return new Surface(format, source.width(), source.height(), 1, Bytes.Mutable.wrap(data));
    }

    private static BlockDecoder decoderFor(TextureFormat format) {
        return switch (format) {
            case BC1_UNORM, BC1_SRGB -> BlockDecoder.bc1(false);
            case BC2_UNORM, BC2_SRGB -> BlockDecoder.bc2();
            case BC3_UNORM, BC3_SRGB -> BlockDecoder.bc3();
            case BC4_UNORM -> BlockDecoder.bc4(false);
            case BC4_SNORM -> BlockDecoder.bc4(true);
            case BC5_UNORM -> BlockDecoder.bc5(false);
            case BC5_SNORM -> BlockDecoder.bc5(true);
            case BC6H_UFLOAT -> BlockDecoder.bc6h(false);
            case BC6H_SFLOAT -> BlockDecoder.bc6h(true);
            case BC7_UNORM, BC7_SRGB -> BlockDecoder.bc7();
            default -> throw new UnsupportedOperationException("Not a compressed format: " + format);
        };
    }

    private static TextureFormat formatFor(TextureFormat format) {
        return switch (format) {
            case BC1_UNORM,
                 BC2_UNORM,
                 BC3_UNORM,
                 BC7_UNORM -> TextureFormat.R8G8B8A8_UNORM;
            case BC1_SRGB,
                 BC2_SRGB,
                 BC3_SRGB,
                 BC7_SRGB -> TextureFormat.R8G8B8A8_SRGB;
            case BC4_UNORM, BC4_SNORM -> TextureFormat.R8_UNORM;
            case BC5_UNORM, BC5_SNORM -> TextureFormat.R8G8_UNORM;
            case BC6H_UFLOAT, BC6H_SFLOAT -> TextureFormat.R16G16B16_SFLOAT;
            default -> throw new UnsupportedOperationException("Not a compressed format: " + format);
        };
    }

    private static void unpackR8(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff++, dstOff += 4) {
                float r = FloatMath.unpackUNorm8(data.get(srcOff));
                dst[dstOff/**/] = srgb ? MathF.srgbToLinear(r) : r;
                dst[dstOff + 1] = 0.0f;
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR8G8(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 2, dstOff += 4) {
                float r = FloatMath.unpackUNorm8(data.get(srcOff/**/));
                float g = FloatMath.unpackUNorm8(data.get(srcOff + 1));
                dst[dstOff/**/] = srgb ? MathF.srgbToLinear(r) : r;
                dst[dstOff + 1] = srgb ? MathF.srgbToLinear(g) : g;
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR8G8B8(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 3, dstOff += 4) {
                float r = FloatMath.unpackUNorm8(data.get(srcOff/**/));
                float g = FloatMath.unpackUNorm8(data.get(srcOff + 1));
                float b = FloatMath.unpackUNorm8(data.get(srcOff + 2));
                dst[dstOff/**/] = srgb ? MathF.srgbToLinear(r) : r;
                dst[dstOff + 1] = srgb ? MathF.srgbToLinear(g) : g;
                dst[dstOff + 2] = srgb ? MathF.srgbToLinear(b) : b;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR8G8B8A8(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 4) {
                float r = FloatMath.unpackUNorm8(data.get(srcOff/**/));
                float g = FloatMath.unpackUNorm8(data.get(srcOff + 1));
                float b = FloatMath.unpackUNorm8(data.get(srcOff + 2));
                float a = FloatMath.unpackUNorm8(data.get(srcOff + 3));
                dst[dstOff/**/] = srgb ? MathF.srgbToLinear(r) : r;
                dst[dstOff + 1] = srgb ? MathF.srgbToLinear(g) : g;
                dst[dstOff + 2] = srgb ? MathF.srgbToLinear(b) : b;
                dst[dstOff + 3] = a; // alpha is always linear
            }
        }
    }

    private static void unpackB8G8R8(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 3, dstOff += 4) {
                float r = FloatMath.unpackUNorm8(data.get(srcOff + 2));
                float g = FloatMath.unpackUNorm8(data.get(srcOff + 1));
                float b = FloatMath.unpackUNorm8(data.get(srcOff/**/));
                dst[dstOff/**/] = srgb ? MathF.srgbToLinear(r) : r;
                dst[dstOff + 1] = srgb ? MathF.srgbToLinear(g) : g;
                dst[dstOff + 2] = srgb ? MathF.srgbToLinear(b) : b;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackB8G8R8A8(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 4) {
                float r = FloatMath.unpackUNorm8(data.get(srcOff + 2));
                float g = FloatMath.unpackUNorm8(data.get(srcOff + 1));
                float b = FloatMath.unpackUNorm8(data.get(srcOff/**/));
                float a = FloatMath.unpackUNorm8(data.get(srcOff + 3));
                dst[dstOff/**/] = srgb ? MathF.srgbToLinear(r) : r;
                dst[dstOff + 1] = srgb ? MathF.srgbToLinear(g) : g;
                dst[dstOff + 2] = srgb ? MathF.srgbToLinear(b) : b;
                dst[dstOff + 3] = a;
            }
        }
    }

    private static void unpackR16Unorm(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 2, dstOff += 4) {
                dst[dstOff/**/] = FloatMath.unpackUNorm16(data.getShort(srcOff));
                dst[dstOff + 1] = 0.0f;
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16B16A16Unorm(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 8, dstOff += 4) {
                dst[dstOff/**/] = FloatMath.unpackUNorm16(data.getShort(srcOff/**/));
                dst[dstOff + 1] = FloatMath.unpackUNorm16(data.getShort(srcOff + 2));
                dst[dstOff + 2] = FloatMath.unpackUNorm16(data.getShort(srcOff + 4));
                dst[dstOff + 3] = FloatMath.unpackUNorm16(data.getShort(srcOff + 6));
            }
        }
    }

    private static void unpackR16Sfloat(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 2, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff));
                dst[dstOff + 1] = 0.0f;
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16Sfloat(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 4, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff/**/));
                dst[dstOff + 1] = Float.float16ToFloat(data.getShort(srcOff + 2));
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16B16Sfloat(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 6, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff/**/));
                dst[dstOff + 1] = Float.float16ToFloat(data.getShort(srcOff + 2));
                dst[dstOff + 2] = Float.float16ToFloat(data.getShort(srcOff + 4));
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16B16A16Sfloat(Surface src, int srcX, int srcY, int srcZ, float[] dst, int tileW, int tileH) {
        Bytes data = src.data();
        for (int row = 0; row < tileH; row++) {
            int srcOff = src.offset(srcX, srcY + row, srcZ);
            int dstOff = row * tileW * 4;
            for (int col = 0; col < tileW; col++, srcOff += 8, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff/**/));
                dst[dstOff + 1] = Float.float16ToFloat(data.getShort(srcOff + 2));
                dst[dstOff + 2] = Float.float16ToFloat(data.getShort(srcOff + 4));
                dst[dstOff + 3] = Float.float16ToFloat(data.getShort(srcOff + 6));
            }
        }
    }
}
