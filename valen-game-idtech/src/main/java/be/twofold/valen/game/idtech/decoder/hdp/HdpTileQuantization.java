package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;

/**
 * Per-channel quantization divisors for one plane, one value per channel for each of DC, LP and HP.
 */
record HdpTileQuantization(
    int[] quantizerDC,
    int[] quantizerLP,
    int[] quantizerHP
) {
    static HdpTileQuantization fromHeader(HdpPlaneHeader header) {
        int numChannels = header.numChannels();
        return new HdpTileQuantization(
            formatQuantizer(header.dcQp(), numChannels),
            formatQuantizer(header.lpQp(), numChannels),
            formatQuantizer(header.hpQp(), numChannels)
        );
    }

    private static int[] formatQuantizer(HdpQp qp, int numChannels) {
        int[] result = new int[numChannels];
        int[] values = qp.quants();
        for (int ch = 0; ch < numChannels; ch++) {
            int index = switch (qp.channelMode()) {
                case UNIFORM -> values[0];
                case SEPARATE -> values[Math.min(ch, 1)];
                default -> throw new UnsupportedOperationException("channelMode=" + qp.channelMode());
            };
            result[ch] = remapQp(index);
        }
        return result;
    }

    // DEVIATION: hdpref uses iShift = 0 for UV DC and UV LP; id-Tech uses 1 across every band and channel.
    private static int remapQp(int iIndex) {
        if (iIndex == 0) {
            return 1;
        }
        if (iIndex < 16) {
            return iIndex << 1;
        }
        int man = 16 + (iIndex & 0x0F);
        int exp = iIndex >> 4;
        return man << exp;
    }

    int numChannels() {
        return quantizerDC.length;
    }
}
