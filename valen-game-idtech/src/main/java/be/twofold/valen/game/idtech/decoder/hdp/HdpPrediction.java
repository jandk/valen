package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;

/**
 * Chooses which neighbour a macroblock predicts from, and records what the next macroblocks will predict against.
 */
final class HdpPrediction {
    private static final int ORIENT_WEIGHT = 4;

    private final HdpCodec codec;

    HdpPrediction(HdpCodec codec) {
        this.codec = codec;
    }

    /**
     * Returns both modes packed as {@code dcMode | (lpMode << 2)}.
     */
    int getPredictionModeDCLP(int channel, HdpColorFormat cf) {
        boolean atLeft = codec.atTileLeftMB;
        boolean atTop = codec.atTileTopMB;

        int iDcMode;
        if (atLeft && atTop) {
            iDcMode = 3;
        } else if (atLeft) {
            iDcMode = 1; // left column, predict from top
        } else if (atTop) {
            iDcMode = 0; // top row, predict from left
        } else {
            iDcMode = pickDcMode(channel, cf);
        }

        int mbX = codec.currentMacroblockColumn;
        int qpIndex = codec.mbInfo.quantizerIndexLP;
        int iAcMode = 2;
        if (iDcMode == 1 && qpIndex == codec.predInfo.top(channel, mbX).iQPIndex) {
            iAcMode = 1;
        } else if (iDcMode == 0 && qpIndex == codec.predInfo.left(channel, mbX).iQPIndex) {
            iAcMode = 0;
        }
        return iDcMode | (iAcMode << 2);
    }

    private int pickDcMode(int channel, HdpColorFormat cf) {
        var rows = codec.predInfo;
        int mbX = codec.currentMacroblockColumn;
        int iL = rows.left(channel, mbX).iDC;
        int iT = rows.top(channel, mbX).iDC;
        int iTL = rows.topLeft(channel, mbX).iDC;
        int strH = Math.abs(iTL - iL);
        int strV = Math.abs(iTL - iT);

        if (cf != HdpColorFormat.Y_ONLY && cf != HdpColorFormat.N_CHANNEL) {
            int uT = rows.top(1, mbX).iDC;
            int uL = rows.left(1, mbX).iDC;
            int uTL = rows.topLeft(1, mbX).iDC;
            int vT = rows.top(2, mbX).iDC;
            int vL = rows.left(2, mbX).iDC;
            int vTL = rows.topLeft(2, mbX).iDC;
            strH = strH * 2 + Math.abs(uTL - uL) + Math.abs(vTL - vL);
            strV = strV * 2 + Math.abs(uTL - uT) + Math.abs(vTL - vT);
        }

        return weigh(strH, strV);
    }

    int getPredictionModeHP(int channel, short[] pCoeffs, HdpColorFormat cf) {
        int coeffsBase = channel * HdpConstants.DC_LP_SIZE;
        int strH = Math.abs(pCoeffs[coeffsBase + 1]) + Math.abs(pCoeffs[coeffsBase + 2]) + Math.abs(pCoeffs[coeffsBase + 3]);
        int strV = Math.abs(pCoeffs[coeffsBase + 4]) + Math.abs(pCoeffs[coeffsBase + 8]) + Math.abs(pCoeffs[coeffsBase + 12]);

        if (cf != HdpColorFormat.Y_ONLY && cf != HdpColorFormat.N_CHANNEL) {
            strH += Math.abs(pCoeffs[HdpConstants.DC_LP_SIZE + 1]) + Math.abs(pCoeffs[2 * HdpConstants.DC_LP_SIZE + 1]);
            strV += Math.abs(pCoeffs[HdpConstants.DC_LP_SIZE + 4]) + Math.abs(pCoeffs[2 * HdpConstants.DC_LP_SIZE + 4]);
        }

        return weigh(strH, strV);
    }

    /**
     * Only predict along an axis when it dominates the other by {@link #ORIENT_WEIGHT}; otherwise don't predict.
     */
    private int weigh(int strH, int strV) {
        if (strH * ORIENT_WEIGHT < strV) {
            return 1;
        }
        if (strV * ORIENT_WEIGHT < strH) {
            return 0;
        }
        return 2;
    }

    void updatePredictionInfo(int numChannels, short[] dcLp) {
        for (int ch = 0; ch < numChannels; ch++) {
            HdpPredictionInfo pPredInfo = codec.predInfo.current(ch, codec.currentMacroblockColumn);
            int base = ch * HdpConstants.DC_LP_SIZE;

            pPredInfo.iDC = dcLp[base];
            pPredInfo.iQPIndex = codec.mbInfo.quantizerIndexLP;

            pPredInfo.iLP[0] = dcLp[base + 1];
            pPredInfo.iLP[1] = dcLp[base + 2];
            pPredInfo.iLP[2] = dcLp[base + 3];
            pPredInfo.iLP[3] = dcLp[base + 4];
            pPredInfo.iLP[4] = dcLp[base + 8];
            pPredInfo.iLP[5] = dcLp[base + 12];
        }
    }
}
