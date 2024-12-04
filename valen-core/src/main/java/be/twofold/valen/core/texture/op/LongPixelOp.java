package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

public interface LongPixelOp extends PixelOp {
    static LongPixelOp source(Surface surface) {
        var data = surface.data();
        int width = surface.width();

        return switch (surface.format()) {
            case R16_SFLOAT -> (x, y) -> {
                int i = (y * width + x) * 2;
                int r = Short.toUnsignedInt(ByteArrays.getShort(data, i));
                return RGBA.rgba16(r, 0, 0, 0x3C00);
            };
            case R16G16_SFLOAT -> (x, y) -> {
                int i = (y * width + x) * 4;
                int r = Short.toUnsignedInt(ByteArrays.getShort(data, i));
                int g = Short.toUnsignedInt(ByteArrays.getShort(data, i + 2));
                return RGBA.rgba16(r, g, 0, 0x3C00);
            };
            case R16G16B16A16_SFLOAT -> (x, y) -> ByteArrays.getLong(data, (y * width + x) * 8);
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    long get(int x, int y);

    default IntPixelOp asInt() {
        return (x, y) -> {
            long rgba = get(x, y);
            float r = Float.float16ToFloat((short) rgba);
            float g = Float.float16ToFloat((short) ((rgba) >> 16));
            float b = Float.float16ToFloat((short) ((rgba) >> 32));
            float a = Float.float16ToFloat((short) ((rgba) >> 48));

            int ri = MathF.packUNorm8(r);
            int gi = MathF.packUNorm8(g);
            int bi = MathF.packUNorm8(b);
            int ai = MathF.packUNorm8(a);

            return RGBA.rgba(ri, gi, bi, ai);
        };
    }
}
