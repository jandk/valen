package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

@FunctionalInterface
public interface F16PixelOp extends PixelOp {
    int BPP = 8;

    static F16PixelOp source(Surface surface) {
        var data = surface.data();
        return switch (surface.format()) {
            case R16_SFLOAT -> index -> {
                int i = index * (BPP / 4);
                int r = Short.toUnsignedInt(ByteArrays.getShort(data, i));
                return rgba16(r, 0, 0, 0x3C00);
            };
            case R16G16_SFLOAT -> index -> {
                int i = index * (BPP * 2 / 4);
                int r = Short.toUnsignedInt(ByteArrays.getShort(data, i));
                int g = Short.toUnsignedInt(ByteArrays.getShort(data, i + 2));
                return rgba16(r, g, 0, 0x3C00);
            };
            case R16G16B16A16_SFLOAT -> index -> ByteArrays.getLong(data, index * BPP);
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    static long rgba16(int r, int g, int b, int a) {
        return (long) r | (long) g << 16 | (long) b << 32 | (long) a << 48;
    }

    long get(int index);

    @Override
    default U8PixelOp asU8() {
        return index -> {
            long rgba = get(index);
            float r = Float.float16ToFloat((short) ((rgba)));
            float g = Float.float16ToFloat((short) ((rgba) >>> 16));
            float b = Float.float16ToFloat((short) ((rgba) >>> 32));
            float a = Float.float16ToFloat((short) ((rgba) >>> 48));

            int ri = MathF.packUNorm8(r);
            int gi = MathF.packUNorm8(g);
            int bi = MathF.packUNorm8(b);
            int ai = MathF.packUNorm8(a);

            return U8PixelOp.rgba(ri, gi, bi, ai);
        };
    }

    @Override
    default Surface toSurface(int width, int height, TextureFormat format) {
        var target = Surface.create(width, height, format);
        var data = target.data();
        switch (format) {
            case R16_SFLOAT -> {
                for (int i = 0, o = 0, area = width * height; i < area; i++, o += 2) {
                    ByteArrays.setShort(data, o, (short) get(i));
                }
            }
            case R16G16_SFLOAT -> {
                for (int i = 0, o = 0, area = width * height; i < area; i++, o += 4) {
                    long rgba = get(i);
                    ByteArrays.setShort(data, o/**/, (short) (rgba));
                    ByteArrays.setShort(data, o + 2, (short) (rgba >>> 16));
                }
            }
            case R16G16B16A16_UNORM -> toPixels(width, height, target.data());
            default -> throw new UnsupportedOperationException("Unsupported format: " + format);
        }
        return target;
    }

    default void toPixels(int width, int height, byte[] data) {
        if (width * height * BPP != data.length) {
            throw new IllegalArgumentException("width * height * " + BPP + " != data.length");
        }
        for (int i = 0, o = 0, lim = width * height; i < lim; i++, o += BPP) {
            ByteArrays.setLong(data, o, get(i));
        }
    }
}
