package be.twofold.valen.game.idtech.decoder.hdp.header;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

/**
 * Quantizer values for one band, either shared across channels or split luma/chroma.
 */
public record HdpQp(
    HdpChannelMode channelMode,
    int[] quants
) {
    static HdpQp read(BitSource bits, int numComponents) throws IOException {
        var channelMode = numComponents > 1
            ? HdpChannelMode.fromValue(bits.read(2))
            : HdpChannelMode.UNIFORM;

        int[] values = switch (channelMode) {
            case UNIFORM -> new int[]{bits.read(8)};
            case SEPARATE -> new int[]{bits.read(8), bits.read(8)};
            default -> throw new UnsupportedOperationException("channelMode=" + channelMode);
        };

        return new HdpQp(channelMode, values);
    }

    @Override
    public String toString() {
        return "HdpQp[" + channelMode + Arrays.toString(quants) + "]";
    }
}
