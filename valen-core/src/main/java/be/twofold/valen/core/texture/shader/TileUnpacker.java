package be.twofold.valen.core.texture.shader;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;

@FunctionalInterface
interface TileUnpacker {
    void unpack(Context ctx, float[] dst);

    static TileUnpacker forSurface(Surface source) {
        // TODO: Replace this with tile decompression (update tinybcdec to support it)
        Surface decompressed = source.format().isCompressed()
            ? decompress(source)
            : source;

        return switch (decompressed.format()) {
            case R8_UNORM, R8_SRGB ->
                (ctx, dst) -> unpackR8(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R8G8_UNORM -> (ctx, dst) -> unpackR8G8(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R8G8B8_UNORM, R8G8B8_SRGB ->
                (ctx, dst) -> unpackR8G8B8(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB ->
                (ctx, dst) -> unpackR8G8B8A8(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case B8G8R8_UNORM, B8G8R8_SRGB ->
                (ctx, dst) -> unpackB8G8R8(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case B8G8R8A8_UNORM, B8G8R8A8_SRGB ->
                (ctx, dst) -> unpackB8G8R8A8(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R16_UNORM ->
                (ctx, dst) -> unpackR16Unorm(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R16G16B16A16_UNORM ->
                (ctx, dst) -> unpackR16G16B16A16Unorm(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R16_SFLOAT ->
                (ctx, dst) -> unpackR16Sfloat(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R16G16_SFLOAT ->
                (ctx, dst) -> unpackR16G16Sfloat(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R16G16B16_SFLOAT ->
                (ctx, dst) -> unpackR16G16B16Sfloat(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R16G16B16A16_SFLOAT ->
                (ctx, dst) -> unpackR16G16B16A16Sfloat(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R10G10B10A2_UNORM ->
                (ctx, dst) -> unpackR10G10B10A2Unorm(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            case R11G11B10_SFLOAT ->
                (ctx, dst) -> unpackR11G11B10Sfloat(decompressed, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            default -> throw new UnsupportedOperationException("No unpacker for: " + decompressed.format());
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

    private static void unpackR8(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff++, dstOff += 4) {
                dst[dstOff/**/] = decode(data.get(srcOff), srgb);
                dst[dstOff + 1] = 0.0f;
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR8G8(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 2, dstOff += 4) {
                dst[dstOff/**/] = decode(data.get(srcOff/**/), srgb);
                dst[dstOff + 1] = decode(data.get(srcOff + 1), srgb);
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR8G8B8(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 3, dstOff += 4) {
                dst[dstOff/**/] = decode(data.get(srcOff/**/), srgb);
                dst[dstOff + 1] = decode(data.get(srcOff + 1), srgb);
                dst[dstOff + 2] = decode(data.get(srcOff + 2), srgb);
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR8G8B8A8(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 4, dstOff += 4) {
                dst[dstOff/**/] = decode(data.get(srcOff/**/), srgb);
                dst[dstOff + 1] = decode(data.get(srcOff + 1), srgb);
                dst[dstOff + 2] = decode(data.get(srcOff + 2), srgb);
                dst[dstOff + 3] = decode(data.get(srcOff + 3), false); // alpha is always linear
            }
        }
    }

    private static void unpackB8G8R8(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 3, dstOff += 4) {
                dst[dstOff/**/] = decode(data.get(srcOff + 2), srgb);
                dst[dstOff + 1] = decode(data.get(srcOff + 1), srgb);
                dst[dstOff + 2] = decode(data.get(srcOff/**/), srgb);
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackB8G8R8A8(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        boolean srgb = src.format().isSrgb();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 4, dstOff += 4) {
                dst[dstOff/**/] = decode(data.get(srcOff + 2), srgb);
                dst[dstOff + 1] = decode(data.get(srcOff + 1), srgb);
                dst[dstOff + 2] = decode(data.get(srcOff/**/), srgb);
                dst[dstOff + 3] = decode(data.get(srcOff + 3), false); // alpha is always linear
            }
        }
    }

    private static void unpackR16Unorm(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 2, dstOff += 4) {
                dst[dstOff/**/] = FloatMath.unpackUNorm16(data.getShort(srcOff));
                dst[dstOff + 1] = 0.0f;
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16B16A16Unorm(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 8, dstOff += 4) {
                dst[dstOff/**/] = FloatMath.unpackUNorm16(data.getShort(srcOff/**/));
                dst[dstOff + 1] = FloatMath.unpackUNorm16(data.getShort(srcOff + 2));
                dst[dstOff + 2] = FloatMath.unpackUNorm16(data.getShort(srcOff + 4));
                dst[dstOff + 3] = FloatMath.unpackUNorm16(data.getShort(srcOff + 6));
            }
        }
    }

    private static void unpackR16Sfloat(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 2, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff));
                dst[dstOff + 1] = 0.0f;
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16Sfloat(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 4, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff/**/));
                dst[dstOff + 1] = Float.float16ToFloat(data.getShort(srcOff + 2));
                dst[dstOff + 2] = 0.0f;
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16B16Sfloat(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 6, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff/**/));
                dst[dstOff + 1] = Float.float16ToFloat(data.getShort(srcOff + 2));
                dst[dstOff + 2] = Float.float16ToFloat(data.getShort(srcOff + 4));
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static void unpackR16G16B16A16Sfloat(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 8, dstOff += 4) {
                dst[dstOff/**/] = Float.float16ToFloat(data.getShort(srcOff/**/));
                dst[dstOff + 1] = Float.float16ToFloat(data.getShort(srcOff + 2));
                dst[dstOff + 2] = Float.float16ToFloat(data.getShort(srcOff + 4));
                dst[dstOff + 3] = Float.float16ToFloat(data.getShort(srcOff + 6));
            }
        }
    }

    private static void unpackR10G10B10A2Unorm(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 4, dstOff += 4) {
                int value = data.getInt(srcOff);
                dst[dstOff/**/] = ((value /* */) & 0x03FF) / 1023.0f;
                dst[dstOff + 1] = ((value >> 10) & 0x03FF) / 1023.0f;
                dst[dstOff + 2] = ((value >> 20) & 0x03FF) / 1023.0f;
                dst[dstOff + 3] = ((value >> 30) & 0x03) / 3.0f;
            }
        }
    }

    private static void unpackR11G11B10Sfloat(Surface src, int x, int y, int z, int width, int height, float[] dst) {
        Bytes data = src.data();
        for (int row = 0; row < height; row++) {
            int srcOff = src.offset(x, y + row, z);
            int dstOff = row * width * 4;
            for (int col = 0; col < width; col++, srcOff += 4, dstOff += 4) {
                int value = data.getInt(srcOff);
                dst[dstOff/**/] = Float.float16ToFloat((short) ((value & 0x0000_07FF) << 4));
                dst[dstOff + 1] = Float.float16ToFloat((short) ((value & 0x003F_F800) >>> (11 - 4)));
                dst[dstOff + 2] = Float.float16ToFloat((short) ((value & 0xFFC0_0000) >>> (22 - 5)));
                dst[dstOff + 3] = 1.0f;
            }
        }
    }

    private static float decode(byte b, boolean srgb) {
        return srgb ? Srgb.srgbByteToLinear(b) : FloatMath.unpackUNorm8(b);
    }
}
