package be.twofold.valen.core.texture.shader;

import be.twofold.tinybcdec.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;

import java.nio.*;
import java.util.function.*;

@FunctionalInterface
interface TileUnpacker {
    void unpack(Context ctx, float[] dst);

    static TileUnpacker forSurface(Surface source, int tileSize) {
        if (source.format().isCompressed()) {
            return new Compressed(source, tileSize);
        }

        return switch (source.format()) {
            case R8_UNORM, R8_SRGB -> (ctx, dst) -> {
                unpackR8(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R8G8_UNORM -> (ctx, dst) -> {
                unpackR8G8(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R8G8B8_UNORM, R8G8B8_SRGB -> (ctx, dst) -> {
                unpackR8G8B8(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R8G8B8A8_UNORM, R8G8B8A8_SRGB -> (ctx, dst) -> {
                unpackR8G8B8A8(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case B8G8R8_UNORM, B8G8R8_SRGB -> (ctx, dst) -> {
                unpackB8G8R8(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case B8G8R8A8_UNORM, B8G8R8A8_SRGB -> (ctx, dst) -> {
                unpackB8G8R8A8(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R16_UNORM -> (ctx, dst) -> {
                unpackR16Unorm(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R16G16B16A16_UNORM -> (ctx, dst) -> {
                unpackR16G16B16A16Unorm(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R16_SFLOAT -> (ctx, dst) -> {
                unpackR16Sfloat(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R16G16_SFLOAT -> (ctx, dst) -> {
                unpackR16G16Sfloat(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R16G16B16_SFLOAT -> (ctx, dst) -> {
                unpackR16G16B16Sfloat(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R16G16B16A16_SFLOAT -> (ctx, dst) -> {
                unpackR16G16B16A16Sfloat(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R10G10B10A2_UNORM -> (ctx, dst) -> {
                unpackR10G10B10A2Unorm(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            case R11G11B10_SFLOAT -> (ctx, dst) -> {
                unpackR11G11B10Sfloat(source, ctx.x, ctx.y, ctx.z, ctx.width, ctx.height, dst);
            };
            default -> throw new UnsupportedOperationException("No unpacker for: " + source.format());
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

    final class Compressed implements TileUnpacker {
        private static final int BYTES_PER_PIXEL = 4 * Float.BYTES;
        private static final int BLOCK_HEIGHT = 4;
        private static final int MAX_SCRATCH_SIZE = 64 * 1024;

        private final ThreadLocal<Decoder> decoders;
        private final Surface source;

        Compressed(Surface source, int tileSize) {
            this(source, tileSize, MAX_SCRATCH_SIZE);
        }

        Compressed(Surface source, int tileSize, int maxScratchSize) {
            var factory = decoderFactory(source.format());
            var maxWidth = Math.min(tileSize, source.width());
            var maxHeight = Math.min(tileSize, source.height());
            var bandHeight = Math.min(maxHeight, bandHeight(maxWidth, maxScratchSize));
            this.decoders = ThreadLocal.withInitial(() -> new Decoder(factory.get(), source, maxWidth, bandHeight));
            this.source = source;
        }

        private static int bandHeight(int width, int maxScratchSize) {
            var rows = maxScratchSize / (width * BYTES_PER_PIXEL);
            return rows < BLOCK_HEIGHT
                ? Math.max(rows, 1)
                : rows - rows % BLOCK_HEIGHT;
        }

        private static Supplier<BlockDecoder> decoderFactory(TextureFormat format) {
            return switch (format) {
                case BC1_UNORM, BC1_SRGB -> () -> BlockDecoder.bc1Float(true);
                case BC1A_UNORM, BC1A_SRGB -> () -> BlockDecoder.bc1Float(false);
                case BC2_UNORM, BC2_SRGB -> BlockDecoder::bc2Float;
                case BC3_UNORM, BC3_SRGB -> BlockDecoder::bc3Float;
                case BC4_UNORM -> () -> BlockDecoder.bc4Float(false);
                case BC4_SNORM -> () -> BlockDecoder.bc4Float(true);
                case BC5_UNORM -> () -> BlockDecoder.bc5Float(false);
                case BC5_SNORM -> () -> BlockDecoder.bc5Float(true);
                case BC6H_UFLOAT -> () -> BlockDecoder.bc6hFloat(false);
                case BC6H_SFLOAT -> () -> BlockDecoder.bc6hFloat(true);
                case BC7_UNORM, BC7_SRGB -> BlockDecoder::bc7Float;
                default -> throw new UnsupportedOperationException("Not a compressed format: " + format);
            };
        }

        @Override
        public void unpack(Context ctx, float[] dst) {
            decoders.get().decode(source, ctx, dst);
            if (source.format().isSrgb()) {
                for (var i = 0; i < ctx.pixelCount() * 4; i += 4) {
                    dst[i/**/] = Srgb.srgbToLinear(dst[i/**/]);
                    dst[i + 1] = Srgb.srgbToLinear(dst[i + 1]);
                    dst[i + 2] = Srgb.srgbToLinear(dst[i + 2]);
                    // alpha is always linear
                }
            }
        }
    }

    final class Decoder {
        private final BlockDecoder decoder;
        private final ByteBuffer scratch;
        private final ByteBuffer src;
        private final int bandHeight;

        Decoder(BlockDecoder decoder, Surface source, int maxWidth, int bandHeight) {
            this.decoder = decoder;
            this.scratch = ByteBuffer
                .allocate(maxWidth * bandHeight * Compressed.BYTES_PER_PIXEL)
                .order(ByteOrder.LITTLE_ENDIAN);
            this.src = source.data() instanceof Bytes.Mutable mutable
                ? mutable.asMutableBuffer()
                : source.data().asBuffer();
            this.bandHeight = bandHeight;
        }

        void decode(Surface source, Context ctx, float[] dst) {
            src.position(source.sliceOffset(ctx.z));
            for (var row = 0; row < ctx.height; row += bandHeight) {
                var rows = Math.min(bandHeight, ctx.height - row);
                decoder.decode(
                    src, source.width(), source.height(),
                    scratch, ctx.width, rows,
                    ctx.x, ctx.y + row, 0, 0,
                    ctx.width, rows
                );
                scratch.asFloatBuffer().get(0, dst, row * ctx.width * 4, rows * ctx.width * 4);
            }
        }
    }
}
