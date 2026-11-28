package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

/**
 * HP band decode for one macroblock: the coded-block pattern first, then run-level coefficients for every sub-block
 * the pattern marks as present.
 */
final class MacroblockHp {
    private static final int[] gFLC0 = {0, 2, 1, 2, 2, 0};
    private static final int[] gOff0 = {0, 4, 2, 8, 12, 1};
    private static final int[] gOut0 = {0, 15, 3, 12, 1, 2, 4, 8, 5, 6, 9, 10, 7, 11, 13, 14};
    private static final int[] aTab = {6, 9, 10, 12};

    static final int[] gSubBlockOffset_444 = {
        0, 64, 16, 80, 128, 192, 144, 208,
        32, 96, 48, 112, 160, 224, 176, 240
    };

    private final HdpCodec codec;
    private final HdpContext ctx;
    private final HdpColorFormat colorFormat;
    private final int numChannels;
    private final RunLevelDecoder hpDecoder;
    private final int[] iLapMean;

    MacroblockHp(HdpCodec codec, HdpContext ctx, HdpColorFormat colorFormat, int numChannels) {
        this.codec = codec;
        this.ctx = ctx;
        this.colorFormat = Check.nonNull(colorFormat, "colorFormat");
        this.numChannels = Check.positive(numChannels, "numChannels");
        this.hpDecoder = new RunLevelDecoder(ctx.adaptHuffHP);
        this.iLapMean = new int[numChannels];
    }

    /**
     * Leaves slot 0 of each sub-block alone: that carries the LP coefficient, scattered in later by dequantization.
     */
    void read(BitSource bits, short[] hp) throws IOException {
        Arrays.fill(iLapMean, 0);

        if (codec.resetAdaptiveScanTotals) {
            HdpAdaptiveScan.resetTotals(ctx.adaptScanHorHP);
            HdpAdaptiveScan.resetTotals(ctx.adaptScanVerHP);
        }
        decodeCbp(bits);
        codec.predictionDec.predictHPCBPDec(colorFormat, numChannels);

        var cn = ctx.adaptCNModelHP;
        boolean isYuv = colorFormat == HdpColorFormat.YUV_444
            || colorFormat == HdpColorFormat.YUV_422
            || colorFormat == HdpColorFormat.YUV_420;

        for (int i = 0; i < numChannels; i++) {
            var scan = codec.mbInfo.predOrientationHP[i] == 1 ? ctx.adaptScanVerHP : ctx.adaptScanHorHP;
            // DEVIATION: hdpref treats every channel > 0 as chroma; the engine only does so for the YUV formats.
            boolean chroma = i > 0 && isYuv;
            int flcBits = cn.fixedLengthCodeBits(i);
            int cbp = codec.mbInfo.actualCBP[i];
            int chBase = i * HdpConstants.MB_SIZE;
            for (int sub = 0; sub < 16; sub++) {
                if ((cbp & (1 << sub)) == 0) {
                    continue;
                }

                int offset = chBase + gSubBlockOffset_444[sub];
                int iNumNonZero = hpDecoder.decodeBlock(bits, chroma, 1);
                hpDecoder.scatter(iNumNonZero, scan, hp, offset, flcBits);
                iLapMean[i] += iNumNonZero;
            }
        }

        cn.updateAdaptiveCoefficientNormalization(iLapMean, numChannels);
        if (codec.adaptContextHuffman) {
            ctx.adaptContextHuffmanHP();
        }
    }

    private void decodeCbp(BitSource bits) throws IOException {
        // For YUV_444: iterate once for Y; the chroma residuals fall out of the same loop body.
        boolean isYuv = colorFormat == HdpColorFormat.YUV_444;
        int iChannel = isYuv ? 1 : numChannels;

        var pAhCbp = ctx.adaptHuffCBP[0];
        var pAhCbp1 = ctx.adaptHuffCBP[1];
        var pAhEx1 = ctx.adaptHuffCBP[2];
        int[] residualCBP = codec.mbInfo.residualCBP;

        for (int i = 0; i < iChannel; i++) {
            int iNumCbp = pAhCbp1.decodeSymbol(bits);
            int iCbpHp = refineCbpHp(bits, iNumCbp);

            int iCbpCY = 0, iCbpCU = 0, iCbpCV = 0;
            for (int iBlock = 0; iBlock < 4; iBlock++) {
                if ((iCbpHp & (1 << iBlock)) == 0) {
                    continue;
                }
                int val = pAhCbp.decodeSymbol(bits) + 1;

                int iNumBlockCbp = 0;
                if (val >= 6) {
                    iNumBlockCbp = 0x10 * (chrCbpHp(bits) + 1);

                    if (val == 9) {
                        val += chrCbpHp(bits);
                    }
                    val -= 6;
                }

                int iCode1 = gOff0[val];
                int flc = gFLC0[val];
                if (flc != 0) {
                    int codeInc = bits.read(flc);
                    iCode1 += codeInc;
                }
                iNumBlockCbp += gOut0[iCode1];

                if (colorFormat == HdpColorFormat.YUV_444) {
                    iCbpCY |= (iNumBlockCbp & 0xF) << (iBlock * 4);
                    for (int k = 0; k < 2; k++) {
                        if (((iNumBlockCbp >> (k + 4)) & 1) == 0) {
                            continue;
                        }
                        int iCode = pAhEx1.decodeSymbol(bits);
                        int iCbpHpChr = refineCbpHp(bits, iCode + 1);
                        if (k == 0) {
                            iCbpCU |= iCbpHpChr << (iBlock * 4);
                        } else {
                            iCbpCV |= iCbpHpChr << (iBlock * 4);
                        }
                    }
                } else {
                    iCbpCY |= iNumBlockCbp << (iBlock * 4);
                }
            }

            residualCBP[i] = iCbpCY;
            if (isYuv) {
                residualCBP[1] = iCbpCU;
                residualCBP[2] = iCbpCV;
            }
        }
    }

    private int refineCbpHp(BitSource bits, int iNum) throws IOException {
        return switch (iNum) {
            case 0 -> 0;
            case 1 -> 1 << bits.read(2);
            case 2 -> {
                int iNumCbp = bits.read(2);
                if (iNumCbp == 0) {
                    yield 3;
                } else if (iNumCbp == 1) {
                    yield 5;
                } else {
                    yield aTab[iNumCbp * 2 + bits.read(1) - 4];
                }
            }
            case 3 -> 0xF ^ (1 << bits.read(2));
            case 4 -> 0xF;
            default -> throw new AssertionError();
        };
    }

    private int chrCbpHp(BitSource bits) throws IOException {
        if (bits.readFlag()) {
            return 0;
        } else if (bits.readFlag()) {
            return 1;
        } else {
            return 2;
        }
    }
}
