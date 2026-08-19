package be.twofold.valen.game.idtech.decoder.hdp;

import wtf.reversed.toolbox.io.*;

import java.io.*;

/**
 * Run-level decoder for one 16-coefficient block, constructed per band; {@link #decodeBlock} reads the (run, level)
 * pairs and {@link #scatter} writes them out through the adaptive scan.
 */
// DEVIATION: the engine fuses both passes into one function; hdpref keeps them separate, as here.
final class RunLevelDecoder {
    private static final int[] FIXED_LEN = {0, 0, 1, 2, 2, 2};
    private static final int[] REMAP = {2, 3, 4, 6, 10, 14};

    private static final int[] gSignificantRunNumBits = {4, 4, 3, 3, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1};
    private static final int[] gSignificantRunIndex = {3, 4, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int[] gSignificantRunOffset = {-3, -3, -3, -3, -2, -2, -1, 0};
    private static final byte[] iRemap = {1, 2, 3, 5, 7, 1, 2, 3, 5, 7, 1, 2, 3, 4, 5};
    private static final byte[] iRunBin = {-1, -1, -1, -1, 2, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0};
    private static final byte[] iRunFixedLength = {0, 0, 1, 1, 3, 0, 0, 1, 1, 2, 0, 0, 0, 0, 1};

    private final HdpAdaptiveHuffman[] adaptHuff;
    private final int[] aRLCoeffs = new int[15 * 2];

    RunLevelDecoder(HdpAdaptiveHuffman[] adaptHuff) {
        this.adaptHuff = adaptHuff;
    }

    int decodeBlock(BitSource bits, boolean chroma, int iLocation) throws IOException {
        int iNumNonzero = 1;
        int iIndex = adaptHuff[chroma ? 3 : 0].decodeSymbol(bits);
        int iSign = -bits.readOne();
        int iSr = iIndex & 1;
        int iSrN = iIndex >> 2;

        int iCont = iSr & iSrN;
        aRLCoeffs[1] = (iIndex & 2) != 0
            ? (decodeSignificantAbsLevel(bits, adaptHuff[6 + iCont]) ^ iSign) - iSign
            : 1 | iSign;
        aRLCoeffs[0] = iSr == 0 ? decodeRun(bits, 15 - iLocation) : 0;
        iLocation += aRLCoeffs[0] + 1;

        while (iSrN != 0) {
            iSr = iSrN & 1;
            aRLCoeffs[iNumNonzero * 2] = iSr == 0 ? decodeRun(bits, 15 - iLocation) : 0;
            iLocation += aRLCoeffs[iNumNonzero * 2] + 1;

            HdpAdaptiveHuffman huff = adaptHuff[(chroma ? 3 : 0) + iCont + 1];
            iIndex = decodeIndex(bits, iLocation, huff);
            iSign = -bits.readOne();

            iSrN = iIndex >> 1;
            iCont &= iSrN;
            aRLCoeffs[iNumNonzero * 2 + 1] = (iIndex & 1) != 0
                ? (decodeSignificantAbsLevel(bits, adaptHuff[6 + iCont]) ^ iSign) - iSign
                : 1 | iSign;
            iNumNonzero++;
        }
        return iNumNonzero;
    }

    private int decodeIndex(BitSource bits, int iLocation, HdpAdaptiveHuffman sAdaptVlc) throws IOException {
        if (iLocation < 15) {
            return sAdaptVlc.decodeSymbol(bits);
        } else if (iLocation == 15) {
            if (bits.readOne() == 0) {
                return 0;
            }
            if (bits.readOne() == 0) {
                return 2;
            }
            return 1 + 2 * bits.readOne();
        } else {
            return bits.readOne();
        }
    }

    void scatter(int iNumNonZero, HdpAdaptiveScan[] scans, short[] coeffs, int coeffsOffset, int flcBits) {
        int iIndex = 1;
        for (int k = 0; k < iNumNonZero; k++) {
            iIndex += aRLCoeffs[k * 2];
            var scan = scans[iIndex];
            // DEVIATION: the engine shifts the level by flcBits as it writes, leaving the LP refinement pass with
            // only the tail bits to add. hdpref carries the level unshifted and shifts there instead.
            coeffs[coeffsOffset + scan.scanIndex] = (short) (aRLCoeffs[k * 2 + 1] << flcBits);
            scan.scanTotal++;
            if (scan.scanTotal > scans[iIndex - 1].scanTotal) {
                scans[iIndex] = scans[iIndex - 1];
                scans[iIndex - 1] = scan;
            }
            iIndex++;
        }
    }

    private static int decodeRun(BitSource bits, int iMaxRun) throws IOException {
        if (iMaxRun < 5) {
            if (iMaxRun != 1) {
                return runValue(bits, iMaxRun);
            } else {
                return 1;
            }
        } else {
            int runIndex = runIndex(bits);
            int iIndex = runIndex + 5 * iRunBin[iMaxRun];
            int iFixed = iRunFixedLength[iIndex];
            int iRun = iRemap[iIndex];
            if (iFixed > 0) {
                iRun += bits.read(iFixed);
            }
            return iRun;
        }
    }

    private static int runValue(BitSource bits, int iMaxRun) throws IOException {
        int width = iMaxRun - 1;
        int peeked = bits.peek(width);
        int run = iMaxRun + gSignificantRunOffset[(~peeked) & 7];
        bits.skip(Math.min(run, width));
        return run;
    }

    private static int runIndex(BitSource bits) throws IOException {
        int peek = bits.peek(4);
        bits.skip(gSignificantRunNumBits[peek]);
        return gSignificantRunIndex[peek];
    }

    static int decodeSignificantAbsLevel(BitSource bits, HdpAdaptiveHuffman huff) throws IOException {
        int iIndex = huff.decodeSymbol(bits);

        if (iIndex < 2) {
            return iIndex + 2; // = REMAP[iIndex]
        }
        if (iIndex < 6) {
            return REMAP[iIndex] + bits.read(FIXED_LEN[iIndex]);
        }

        int iFixed = bits.read(4) + 4;
        if (iFixed == 19) {
            iFixed += bits.read(2);
            if (iFixed == 22) {
                iFixed += bits.read(3);
            }
        }
        return 2 + (1 << iFixed) + bits.read(iFixed);
    }
}
