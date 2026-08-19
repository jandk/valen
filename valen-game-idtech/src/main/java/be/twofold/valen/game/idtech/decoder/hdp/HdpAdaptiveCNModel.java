package be.twofold.valen.game.idtech.decoder.hdp;

import java.util.*;

/**
 * Per-channel count of fixed-length tail bits appended to each coefficient, nudged up or down by macroblock busyness.
 */
final class HdpAdaptiveCNModel {
    private static final int[] aWeight0 = {240, 12, 1};

    private final int[] fixedLengthCodeState = new int[3];
    private final int[] fixedLengthCodeBits = new int[3];
    private final HdpBand band;

    HdpAdaptiveCNModel(HdpBand band, int fixedLengthCodeBits) {
        this.band = band;
        Arrays.fill(this.fixedLengthCodeBits, fixedLengthCodeBits);
    }

    int fixedLengthCodeBits(int ch) {
        return fixedLengthCodeBits[ch];
    }

    void updateAdaptiveCoefficientNormalization(int[] iLaplacianMean, int numChannels) {
        for (int ch = 0; ch < numChannels; ch++) {
            // DEVIATION: hdpref weights only iLaplacianMean[0]; the engine weights every channel.
            iLaplacianMean[ch] *= aWeight0[band.ordinal() - 1];
        }

        for (int ch = 0; ch < numChannels; ch++) {
            int iLM = iLaplacianMean[ch];
            int iMS = fixedLengthCodeState[ch];
            int iDelta = (iLM - 70) >> 2;
            int iBits = fixedLengthCodeBits[ch];

            if (iDelta <= -8) {
                iDelta = Math.max(iDelta + 4, -16);
                iMS += iDelta;

                if (iMS < -8) {
                    iMS = (iBits == 0) ? -8 : 0;
                    iBits = Math.max(iBits - 1, 0);
                }
            } else if (iDelta >= 8) {
                iDelta = Math.min(iDelta - 4, 15);
                iMS += iDelta;

                if (iMS > 8) {
                    iMS = iBits >= 15 ? 8 : 0;
                    iBits = Math.min(iBits + 1, 15);
                }
            }

            fixedLengthCodeState[ch] = iMS;
            fixedLengthCodeBits[ch] = iBits;
        }
    }
}
