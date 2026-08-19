package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

/**
 * LP band decode for one macroblock: the coded-block pattern, run-level coefficients, then the tail-bit refinement.
 */
final class MacroblockLp {
    private final short[] lp = new short[HdpConstants.CHANNELS * 16];
    private final int[] iLapMean = new int[HdpConstants.CHANNELS];
    private final HdpContext ctx;
    private final RunLevelDecoder lpDecoder;
    private final HdpColorFormat colorFormat;
    private final int numChannels;

    MacroblockLp(HdpContext ctx, HdpColorFormat colorFormat, int numChannels) {
        this.ctx = ctx;
        this.colorFormat = Check.nonNull(colorFormat, "colorFormat");
        this.numChannels = Check.positive(numChannels, "numChannels");
        this.lpDecoder = new RunLevelDecoder(ctx.adaptHuffLP);
    }

    short[] lp() {
        return lp;
    }

    void read(BitSource bits, boolean bResetTotals, boolean bResetContext) throws IOException {
        Arrays.fill(lp, (short) 0);
        Arrays.fill(iLapMean, 0);

        if (bResetTotals) {
            HdpAdaptiveScan.resetTotals(ctx.adaptScanLP);
        }

        int iCbp = decodeCbp(bits);
        for (int n = 0; n < numChannels; n++) {
            int flcBits = ctx.adaptCNModelLP.fixedLengthCodeBits(n);
            if (((iCbp >> n) & 1) != 0) {
                // DEVIATION: hdpref treats every channel > 0 as chroma; the engine only does so for YUV_444.
                boolean chroma = colorFormat == HdpColorFormat.YUV_444 && n > 0;
                int iNumNonzero = lpDecoder.decodeBlock(bits, chroma, 1);
                iLapMean[n] = iNumNonzero;
                lpDecoder.scatter(iNumNonzero, ctx.adaptScanLP, lp, n * HdpConstants.DC_LP_SIZE, flcBits);
            }
            extendFlc(bits, lp, n, flcBits);
        }

        ctx.adaptCNModelLP.updateAdaptiveCoefficientNormalization(iLapMean, numChannels);
        if (bResetContext) {
            ctx.adaptContextHuffmanLP();
        }
    }

    private int decodeCbp(BitSource bits) throws IOException {
        if (colorFormat == HdpColorFormat.YUV_444) {
            int iCountM = ctx.adaptCBPModelLP.maxCount;
            int iCountZ = ctx.adaptCBPModelLP.zeroCount;
            int iMax = numChannels * 4 - 5;

            int iCbp;
            if (iCountZ <= 0 || iCountM < 0) {
                iCbp = 0;
                if (bits.readFlag()) {
                    iCbp = 1;
                    int k = bits.read(numChannels - 1);
                    if (k != 0) {
                        iCbp = k * 2 + bits.read(1);
                    }
                }
                if (iCountM < iCountZ) {
                    iCbp = iMax - iCbp;
                }
            } else {
                iCbp = bits.read(numChannels);
            }

            iCountM += 1 - (iCbp == iMax ? 4 : 0);
            iCountZ += 1 - (iCbp == 0 ? 4 : 0);
            ctx.adaptCBPModelLP.maxCount = Math.clamp(iCountM, -8, 7);
            ctx.adaptCBPModelLP.zeroCount = Math.clamp(iCountZ, -8, 7);
            return iCbp;
        } else {
            int iCbp = 0;
            for (int n = 0; n < numChannels; n++) {
                iCbp |= bits.readOne() << n;
            }
            return iCbp;
        }
    }

    private void extendFlc(BitSource bits, short[] coeffs, int channel, int flcBits) throws IOException {
        int base = channel * HdpConstants.DC_LP_SIZE;
        for (int k = 1; k < HdpConstants.DC_LP_SIZE; k++) {
            coeffs[base + k] = refineLp(bits, coeffs[base + k], flcBits);
        }
    }

    private short refineLp(BitSource bits, int iCoeff, int iModelBits) throws IOException {
        int coeffRef = bits.read(iModelBits);
        // DEVIATION: hdpref shifts the coefficient by iModelBits here, the engine already did so when scattering
        if (iCoeff > 0) {
            iCoeff += coeffRef;
        } else if (iCoeff < 0) {
            iCoeff -= coeffRef;
        } else {
            iCoeff = coeffRef;
            if (iCoeff != 0) {
                boolean signFlag = bits.readFlag();
                if (signFlag) {
                    iCoeff = -iCoeff;
                }
            }
        }
        return (short) iCoeff;
    }
}
