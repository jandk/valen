package be.twofold.valen.game.idtech.decoder.hdp;

import java.util.*;

/**
 * Per-channel, per-MB DC + LP samples and CBP, retained as prediction context for neighbouring macroblocks.
 */
final class HdpPredictionInfo {
    int iQPIndex;
    int iCBP;
    int iDC;
    final int[] iLP = new int[6];

    void reset() {
        iQPIndex = 0;
        iCBP = 0;
        iDC = 0;
        Arrays.fill(iLP, 0);
    }
}
