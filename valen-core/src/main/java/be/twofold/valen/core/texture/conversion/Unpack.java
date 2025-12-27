package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;
import wtf.reversed.toolbox.collect.*;

import java.util.function.*;

final class Unpack extends Conversion {

    @Override
    Surface apply(Surface surface, TextureFormat dstFormat) {
        var srcFormat = surface.format();
        if (srcFormat == dstFormat) {
            return surface;
        }

        var unpacker = Unpackers.getConverter(srcFormat, dstFormat);
        if (unpacker == null) {
            unpacker = switch (srcFormat) {
                case R10G10B10A2_UNORM -> switch (dstFormat) {
                    case R8G8B8A8_UNORM, B8G8R8A8_UNORM -> Unpack::unpackR10G10B10A2UnormToR8G8B8A8Unorm;
                    case R16G16B16A16_UNORM -> Unpack::unpackR10G10B10A2UnormToR16G16B16A16Unorm;
                    default -> null;
                };
                default -> null;
            };
        }

        if (unpacker == null) {
            return surface;
        }

        return unpack(surface, dstFormat, unpacker);
    }

    private static Surface unpack(Surface source, TextureFormat dstFormat, BiConsumer<byte[], byte[]> operation) {
        var target = Surface.create(source.width(), source.height(), dstFormat);

        var src = source.data();
        var dst = target.data();
        operation.accept(src, dst);

        return target;
    }

    private static void unpackR10G10B10A2UnormToR8G8B8A8Unorm(byte[] src, byte[] dst) {
        var srcBytes = Bytes.wrap(src);
        var dstBytes = Bytes.Mutable.wrap(dst);
        for (int i = 0, o = 0; i < srcBytes.length(); i += 4, o += 4) {
            int pixel = srcBytes.getInt(i);
            int r = (pixel/*  */) & 0x3FF;
            int g = (pixel >> 10) & 0x3FF;
            int b = (pixel >> 20) & 0x3FF;
            int a = (pixel >> 30) & 0x003;
            dstBytes.set(o/**/, (byte) (r >> 2));
            dstBytes.set(o + 1, (byte) (g >> 2));
            dstBytes.set(o + 2, (byte) (b >> 2));
            dstBytes.set(o + 3, (byte) (a * 0x55));
        }
    }

    private static void unpackR10G10B10A2UnormToR16G16B16A16Unorm(byte[] src, byte[] dst) {
        var srcBytes = Bytes.wrap(src);
        var dstBytes = Bytes.Mutable.wrap(dst);
        for (int i = 0, o = 0; i < srcBytes.length(); i += 4, o += 8) {
            int pixel = srcBytes.getInt(i);
            int r = (pixel/*  */) & 0x3FF;
            int g = (pixel >> 10) & 0x3FF;
            int b = (pixel >> 20) & 0x3FF;
            int a = (pixel >> 30) & 0x003;
            dstBytes.setShort(o/**/, (short) (r << 6 | r >> 4));
            dstBytes.setShort(o + 2, (short) (g << 6 | g >> 4));
            dstBytes.setShort(o + 4, (short) (b << 6 | b >> 4));
            dstBytes.setShort(o + 6, (short) (a * 0x5555));
        }
    }
}
