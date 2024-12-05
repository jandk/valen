package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

@FunctionalInterface
public interface IntPixelOp extends PixelOp {
    static IntPixelOp source(Surface surface) {
        var data = surface.data();
        int width = surface.width();

        return switch (surface.format()) {
            case R8_UNORM -> (x, y) -> {
                int i = (y * width + x);
                int r = Byte.toUnsignedInt(data[i]);
                return RGBA.rgb(r, r, r);
            };
            case R8G8_UNORM -> (x, y) -> {
                int i = (y * width + x) * 2;
                int r = Byte.toUnsignedInt(data[i]);
                int g = Byte.toUnsignedInt(data[i + 1]);
                return RGBA.rgb(r, g, 0);
            };
            case R8G8B8_UNORM -> (x, y) -> {
                int i = (y * width + x) * 3;
                int r = Byte.toUnsignedInt(data[i]);
                int g = Byte.toUnsignedInt(data[i + 1]);
                int b = Byte.toUnsignedInt(data[i + 2]);
                return RGBA.rgb(r, g, b);
            };
            case R8G8B8A8_UNORM -> (x, y) -> ByteArrays.getInt(data, (y * width + x) * 4);
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    static IntPixelOp combine(ChannelOp rOp, ChannelOp gOp, ChannelOp bOp, ChannelOp aOp) {
        return (x, y) -> {
            int r = rOp.get(x, y);
            int g = gOp.get(x, y);
            int b = bOp.get(x, y);
            int a = aOp.get(x, y);
            return RGBA.rgba(r, g, b, a);
        };
    }

    int get(int x, int y);

    @Override
    default IntPixelOp asInt() {
        return this;
    }

    default ChannelOp red() {
        return (x, y) -> RGBA.r(get(x, y));
    }

    default ChannelOp green() {
        return (x, y) -> RGBA.g(get(x, y));
    }

    default ChannelOp blue() {
        return (x, y) -> RGBA.b(get(x, y));
    }

    default ChannelOp alpha() {
        return (x, y) -> RGBA.a(get(x, y));
    }

    default IntPixelOp invert() {
        return (x, y) -> {
            int rgba = get(x, y);
            int r = 255 - RGBA.r(rgba);
            int g = 255 - RGBA.g(rgba);
            int b = 255 - RGBA.b(rgba);
            int a = /*255 -*/ RGBA.a(rgba);
            return RGBA.rgba(r, g, b, a);
        };
    }

    default IntPixelOp premultiplyAlpha() {
        return (x, y) -> {
            int rgba = get(x, y);
            int a = RGBA.a(rgba);
            int r = RGBA.r(rgba) * a / 255;
            int g = RGBA.g(rgba) * a / 255;
            int b = RGBA.b(rgba) * a / 255;
            return RGBA.rgba(r, g, b, a);
        };
    }

    default IntPixelOp swizzleBGRA() {
        return (x, y) -> {
            int pixel = get(x, y);
            return pixel & 0xFF00FF00
                | (pixel & 0x00FF0000) >> 16
                | (pixel & 0x000000FF) << 16;
        };
    }

    default byte[] toPixels(int width, int height) {
        var data = new byte[width * height * 4];
        toPixels(width, height, data);
        return data;
    }

    default void toPixels(int width, int height, byte[] data) {
        if (width * height * 4 != data.length) {
            throw new IllegalArgumentException("width * height * 4 != data.length");
        }
        for (int y = 0, o = 0; y < height; y++) {
            for (int x = 0; x < width; x++, o += 4) {
                ByteArrays.setInt(data, o, get(x, y));
            }
        }
    }

    default Surface toSurface(int width, int height) {
        return new Surface(
            width, height,
            TextureFormat.R8G8B8A8_UNORM,
            toPixels(width, height)
        );
    }
}
