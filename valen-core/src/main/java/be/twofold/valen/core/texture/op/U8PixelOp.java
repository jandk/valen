package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

@FunctionalInterface
public interface U8PixelOp extends PixelOp {
    int BPP = 4;

    static U8PixelOp source(Surface surface) {
        var data = surface.data();

        return switch (surface.format()) {
            case R8_UNORM -> index -> {
                int r = Byte.toUnsignedInt(data[index]);
                return rgba(r, r, r, 0xFF);
            };
            case R8G8_UNORM -> index -> {
                int i = index * 2;
                int r = Byte.toUnsignedInt(data[i]);
                int g = Byte.toUnsignedInt(data[i + 1]);
                return rgba(r, g, 0, 0xFF);
            };
            case R8G8B8_UNORM -> index -> {
                int i = index * 3;
                int r = Byte.toUnsignedInt(data[i]);
                int g = Byte.toUnsignedInt(data[i + 1]);
                int b = Byte.toUnsignedInt(data[i + 2]);
                return rgba(r, g, b, 0xFF);
            };
            case R8G8B8A8_UNORM -> index -> ByteArrays.getInt(data, index * 4);
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    static U8PixelOp combine(U8ChannelOp rOp, U8ChannelOp gOp, U8ChannelOp bOp, U8ChannelOp aOp) {
        return index -> {
            int r = rOp.get(index);
            int g = gOp.get(index);
            int b = bOp.get(index);
            int a = aOp.get(index);
            return rgba(r, g, b, a);
        };
    }

    private static int r(int rgba) {
        return (rgba) & 0xFF;
    }

    private static int g(int rgba) {
        return (rgba >>> 8) & 0xFF;
    }

    private static int b(int rgba) {
        return (rgba >>> 16) & 0xFF;
    }

    private static int a(int rgba) {
        return (rgba >>> 24) & 0xFF;
    }

    static int rgba(int r, int g, int b, int a) {
        return r | g << 8 | b << 16 | a << 24;
    }

    int get(int index);

    @Override
    default U8PixelOp asU8() {
        return this;
    }

    @Override
    default Surface toSurface(int width, int height, TextureFormat format) {
        var target = Surface.create(width, height, format);
        var data = target.data();
        switch (format) {
            case R8_UNORM -> {
                for (int i = 0, area = width * height; i < area; i++) {
                    data[i] = (byte) (get(i) & 0xFF);
                }
            }
            case R8G8B8_UNORM -> {
                for (int i = 0, o = 0, area = width * height; i < area; i++, o += 3) {
                    int rgba = get(i);
                    data[o/**/] = (byte) ((rgba /*  */) & 0xFF);
                    data[o + 1] = (byte) ((rgba >>> +8) & 0xFF);
                    data[o + 2] = (byte) ((rgba >>> 16) & 0xFF);
                }
            }
            case R8G8B8A8_UNORM -> toPixels(width, height, target.data());
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        }
        return target;
    }

    default void toPixels(int width, int height, byte[] data) {
        if (width * height * BPP != data.length) {
            throw new IllegalArgumentException("width * height * " + BPP + " != data.length");
        }
        for (int i = 0, o = 0, lim = width * height; i < lim; i++, o += BPP) {
            ByteArrays.setInt(data, o, get(i));
        }
    }

    default U8ChannelOp red() {
        return index -> r(get(index));
    }

    default U8ChannelOp green() {
        return index -> g(get(index));
    }

    default U8ChannelOp blue() {
        return index -> b(get(index));
    }

    default U8ChannelOp alpha() {
        return index -> a(get(index));
    }

    default U8PixelOp premultiplyAlpha() {
        return index -> {
            int rgba = get(index);
            int a = a(rgba);
            int r = r(rgba) * a / 255;
            int g = g(rgba) * a / 255;
            int b = b(rgba) * a / 255;
            return rgba(r, g, b, a);
        };
    }

    default U8PixelOp swizzleBGRA() {
        return index -> {
            int pixel = get(index);
            return pixel & 0xFF00FF00
                | (pixel & 0x00FF0000) >> 16
                | (pixel & 0x000000FF) << 16;
        };
    }

    final class ReconstructZ implements U8PixelOp {
        private static final int[] NORMAL = new int[256 * 256];

        static {
            for (int y = 0; y < 256; y++) {
                for (int x = 0; x < 256; x++) {
                    float xx = MathF.unpackUNorm8Normal((byte) x);
                    float yy = MathF.unpackUNorm8Normal((byte) y);
                    float sq = xx * xx + yy * yy;
                    float nz = (float) Math.sqrt(1.0f - Math.min(1.0f, Math.max(sq, 0.0f)));
                    NORMAL[y * 256 + x] = Byte.toUnsignedInt(MathF.packUNorm8Normal(nz));
                }
            }
        }

        private final U8PixelOp source;

        private ReconstructZ(U8PixelOp source) {
            this.source = Check.notNull(source, "source");
        }

        @Override
        public int get(int index) {
            int rgba = source.get(index);
            int r = r(rgba);
            int g = g(rgba);
            int b = NORMAL[g * 256 + r];
            return rgba(r, g, b, 0xFF);
        }
    }
}
