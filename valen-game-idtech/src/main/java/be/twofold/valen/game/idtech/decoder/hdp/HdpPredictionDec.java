package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;

/**
 * Folds neighbour context back in, turning the residuals the bitstream carries into absolute coefficients.
 */
final class HdpPredictionDec {
    private final HdpCodec codec;

    HdpPredictionDec(HdpCodec codec) {
        this.codec = codec;
    }

    /**
     * Must run between the LP and HP bands: the HP orientation is chosen from LP coefficients that have to be absolute.
     */
    void predictDCLPDec(HdpColorFormat cf, int numChannels, short[] dcLp) {
        var prediction = codec.prediction;

        int sharedDcLp = prediction.getPredictionModeDCLP(0, cf);
        for (int ch = 0; ch < numChannels; ch++) {
            // DEVIATION: hdpref reuses channel 0's mode everywhere; the engine re-picks per channel for N_CHANNEL.
            int mode = (cf == HdpColorFormat.N_CHANNEL && ch > 0)
                ? prediction.getPredictionModeDCLP(ch, cf)
                : sharedDcLp;
            applyDcLpMode(mode, ch, dcLp);
        }

        int sharedHp = prediction.getPredictionModeHP(0, dcLp, cf);
        for (int ch = 0; ch < numChannels; ch++) {
            // DEVIATION: as above, per-channel modes for N_CHANNEL only.
            int mode = (cf == HdpColorFormat.N_CHANNEL && ch > 0)
                ? prediction.getPredictionModeHP(ch, dcLp, cf)
                : sharedHp;
            codec.mbInfo.predOrientationHP[ch] = 2 - mode;
        }
    }

    @SuppressWarnings("lossy-conversions")
    private void applyDcLpMode(int mode, int ch, short[] pOrg) {
        int iDcPredMode = mode & 0x3;
        int iAdPredMode = mode & 0xC;
        int base = ch * HdpConstants.DC_LP_SIZE;

        var rows = codec.predInfo;
        int mbX = codec.currentMacroblockColumn;

        switch (iDcPredMode) {
            case 0 -> pOrg[base] += rows.left(ch, mbX).iDC;
            case 1 -> pOrg[base] += rows.top(ch, mbX).iDC;
            case 2 -> pOrg[base] += (rows.left(ch, mbX).iDC + rows.top(ch, mbX).iDC) >> 1;
        }

        if (iAdPredMode == 4) {
            int[] pRef = rows.top(ch, mbX).iLP;
            pOrg[base + +4] += pRef[3];
            pOrg[base + +8] += pRef[4];
            pOrg[base + 12] += pRef[5];
        } else if (iAdPredMode == 0) {
            int[] pRef = rows.left(ch, mbX).iLP;
            pOrg[base + 1] += pRef[0];
            pOrg[base + 2] += pRef[1];
            pOrg[base + 3] += pRef[2];
        }
    }

    /**
     * Folds three HP coefficients of each sub-block into its neighbour within the same macroblock.
     */
    void predictHPDec(HdpColorFormat cf, int numChannels, short[] hp) {
        int iChannels = (cf == HdpColorFormat.YUV_420 || cf == HdpColorFormat.YUV_422) ? 1 : numChannels;
        for (int i = 0; i < iChannels; i++) {
            int iACPredMode = 2 - codec.mbInfo.predOrientationHP[i];
            switch (iACPredMode) {
                case 0 -> predictFromLeft(hp, i);
                case 1 -> predictFromTop(hp, i);
            }
        }
    }

    private void predictFromTop(short[] coeffs, int channel) {
        int base = channel * HdpConstants.MB_SIZE;
        for (int i = 1; i < 16; i++) {
            if ((i & 3) == 0) { // skip 4, 8, 12 - those start a new sub-block row
                continue;
            }

            int pOrg = base + 16 * i;
            int pRef = pOrg - 16;

            coeffs[pOrg + +2] += coeffs[pRef + +2];
            coeffs[pOrg + +9] += coeffs[pRef + +9];
            coeffs[pOrg + 10] += coeffs[pRef + 10];
        }
    }

    private void predictFromLeft(short[] coeffs, int channel) {
        int base = channel * HdpConstants.MB_SIZE;
        for (int i = 64; i < 256; i += 16) {
            int pOrg = base + i;
            int pRef = pOrg - 64;

            coeffs[pOrg + 1] += coeffs[pRef + 1];
            coeffs[pOrg + 5] += coeffs[pRef + 5];
            coeffs[pOrg + 6] += coeffs[pRef + 6];
        }
    }

    void predictHPCBPDec(HdpColorFormat cf, int numChannels) {
        int iChannels = (cf == HdpColorFormat.YUV_420 || cf == HdpColorFormat.YUV_422) ? 1 : numChannels;
        for (int i = 0; i < iChannels; i++) {
            int actual = predCbpCDec(i, codec.mbInfo.residualCBP[i]);
            codec.mbInfo.actualCBP[i] = actual;
            codec.predInfo.current(i, codec.currentMacroblockColumn).iCBP = actual;
        }
    }

    /**
     * Adaptive model picks: spatial (0), identity (1) or full inversion (2).
     */
    private int predCbpCDec(int i, int iDiffCbpHp) {
        int iCbp = iDiffCbpHp;

        int state = codec.ctx.adaptCBPModelHP.state(i);
        if (state == 0) {
            if (codec.atTileLeftMB) {
                if (codec.atTileTopMB) {
                    iCbp ^= 1;
                } else {
                    int iTopCbp = codec.predInfo.top(i, codec.currentMacroblockColumn).iCBP;
                    iCbp ^= (iTopCbp >> 10) & 1; // left: top(10) => 0
                }
            } else {
                int iLeftCbp = codec.predInfo.left(i, codec.currentMacroblockColumn).iCBP;
                iCbp ^= (iLeftCbp >> 5) & 1; // left(5) => 0
            }

            iCbp ^= 0x02 & (iCbp << 1); // 0 => 1
            iCbp ^= 0x10 & (iCbp << 3); // 1 => 4
            iCbp ^= 0x20 & (iCbp << 1); // 4 => 5

            iCbp ^= (iCbp & 0x0033) << 2;
            iCbp ^= (iCbp & 0x00CC) << 6;
            iCbp ^= (iCbp & 0x3300) << 2;
        } else if (state == 2) {
            iCbp ^= 0xFFFF;
        }

        codec.ctx.adaptCBPModelHP.update(i, Integer.bitCount(iCbp & 0xFFFF));
        return iCbp;
    }
}
