package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

@FunctionalInterface
public interface ChannelOp {
    static ChannelOp constant(int value) {
        return (_, _) -> value;
    }

    static ChannelOp source(Surface surface) {
        var data = surface.data();
        int width = surface.width();

        return switch (surface.format()) {
            case R8_UNORM -> (x, y) -> Byte.toUnsignedInt(data[y * width + x]);
            case R16_SFLOAT -> (x, y) -> Short.toUnsignedInt(ByteArrays.getShort(data, (y * width + x) * 2));
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    default IntPixelOp rgba() {
        return (x, y) -> get(x, y) * 0x010101 | 0xFF000000;
    }

    int get(int x, int y);
}
