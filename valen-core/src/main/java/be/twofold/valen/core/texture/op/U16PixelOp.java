package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

import static be.twofold.valen.core.texture.op.F16PixelOp.*;

@FunctionalInterface
public interface U16PixelOp extends PixelOp {
    int BPP = 8;

    static U16PixelOp source(Surface surface) {
        var data = surface.data();
        return switch (surface.format()) {
            case R16_UNORM -> index -> {
                int i = index * (BPP / 4);
                int r = Short.toUnsignedInt(ByteArrays.getShort(data, i));
                return rgba16(r, 0, 0, 0xFFFF);
            };
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    long get(int index);

    @Override
    default U8PixelOp asU8() {
        return index -> {
            long rgba = get(index);
            int r = (int) (rgba >>> +8) & 0xFF;
            int g = (int) (rgba >>> 24) & 0xFF;
            int b = (int) (rgba >>> 40) & 0xFF;
            int a = (int) (rgba >>> 56) & 0xFF;
            return U8PixelOp.rgba(r, g, b, a);
        };
    }

    @Override
    default Surface toSurface(int width, int height, TextureFormat format) {
        var target = Surface.create(width, height, format);
        var data = target.data();
        switch (format) {
            case R16_UNORM -> {
                for (int i = 0, o = 0, area = width * height; i < area; i++, o += 2) {
                    ByteArrays.setShort(data, o, (short) get(i));
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
