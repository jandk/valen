package be.twofold.valen.core.texture.op;

import be.twofold.valen.core.texture.*;
import be.twofold.valen.core.util.*;

@FunctionalInterface
public interface F16ChannelOp {
    static F16ChannelOp source(Surface surface) {
        var data = surface.data();
        return switch (surface.format()) {
            case R16_SFLOAT -> index -> Short.toUnsignedInt(ByteArrays.getShort(data, index * 2));
            default -> throw new UnsupportedOperationException("Unsupported format: " + surface.format());
        };
    }

    int get(int index);
}
