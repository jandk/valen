package be.twofold.valen.core.texture.conversion;

import be.twofold.valen.core.texture.*;

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

}
