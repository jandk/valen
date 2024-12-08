package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.texture.*;

@FunctionalInterface
public interface U8ChannelOp {
    static U8ChannelOp constant(int value) {
        return _ -> value;
    }

    static U8ChannelOp source(Surface surface) {
        var data = surface.data();
        return switch (surface.format()) {
            case R8_UNORM -> index -> Byte.toUnsignedInt(data[index]);
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    int get(int index);

    default U8ChannelOp invert() {
        return index -> 255 - get(index);
    }

    default U8PixelOp rgba() {
        return index -> get(index) * 0x010101 | 0xFF000000;
    }
}
