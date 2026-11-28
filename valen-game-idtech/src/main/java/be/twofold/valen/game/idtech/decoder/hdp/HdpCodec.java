package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

/**
 * One plane's decoder: entropy state, the three band decoders, the predictors and the dequantizer for that plane.
 */
final class HdpCodec {
    final HdpColorFormat colorFormat;
    final int numChannels;
    final int channelOffset;
    final int widthInMacroblocks;

    final HdpContext ctx;

    final HdpMacroblockInfo mbInfo;
    final PredInfoRows predInfo;

    final MacroblockDc macroblockDc;
    final MacroblockLp macroblockLp;
    final MacroblockHp macroblockHp;

    final HdpPrediction prediction;
    final HdpPredictionDec predictionDec;
    final HdpQuantizationDec dequantizer;

    /**
     * Per-macroblock scratch, allocated once per plane instead of per call. Sized to the maximum channel count, so
     * one allocation fits any plane.
     */
    private final short[] dcLp = new short[HdpConstants.CHANNELS * HdpConstants.DC_LP_SIZE];
    private final short[] hp = new short[HdpConstants.CHANNELS * HdpConstants.MB_SIZE];
    private final short[] dqMb = new short[HdpConstants.CHANNELS * HdpConstants.MB_SIZE];

    /**
     * Stride between channels in the caller's row buffers: two macroblock rows, since they ping-pong.
     */
    private final int rowBufsChannelStride;

    HdpCodec nextCodec;

    int currentMacroblockColumn;
    int currentMacroblockRow;
    boolean atTileLeftMB;
    boolean atTileTopMB;
    boolean resetAdaptiveScanTotals;
    boolean adaptContextHuffman;

    HdpCodec(HdpPlaneHeader header, int widthInMacroblocks, int channelOffset) {
        this.colorFormat = header.colorFormat();
        this.numChannels = header.numChannels();
        this.channelOffset = channelOffset;
        this.widthInMacroblocks = widthInMacroblocks;
        this.rowBufsChannelStride = 2 * widthInMacroblocks * HdpConstants.MB_SIZE;

        this.mbInfo = new HdpMacroblockInfo(numChannels);
        this.predInfo = new PredInfoRows(numChannels, widthInMacroblocks);
        this.ctx = new HdpContext(colorFormat);

        // Must precede the band decoders: MacroblockHp reaches back through the codec for the predictor.
        this.prediction = new HdpPrediction(this);
        this.predictionDec = new HdpPredictionDec(this);
        this.dequantizer = new HdpQuantizationDec(HdpTileQuantization.fromHeader(header));

        this.macroblockDc = new MacroblockDc(ctx, colorFormat, numChannels);
        this.macroblockLp = new MacroblockLp(ctx, colorFormat, numChannels);
        this.macroblockHp = new MacroblockHp(this, ctx, colorFormat, numChannels);
    }

    // The tile-relative offsets the engine subtracts here are zero throughout: id-Tech emits one tile per plane.
    void updateTilePos(int mbX, int mbY) {
        currentMacroblockColumn = mbX;
        currentMacroblockRow = mbY;
        atTileLeftMB = mbX == 0;
        atTileTopMB = mbY == 0;
        resetAdaptiveScanTotals = (mbX & 0xF) == 0;
        adaptContextHuffman = (mbX & 0xF) == 0 || mbX + 1 == widthInMacroblocks;
    }

    /**
     * {@code rowBufs} is shared with the other codecs on this bitstream; each writes only its own channel slice,
     * starting at {@link #channelOffset}.
     */
    void decodeMacroblock(BitSource bits, int currRowBase, int[] rowBufs) throws IOException {
        Arrays.fill(dcLp, (short) 0);
        Arrays.fill(hp, (short) 0);
        decodeMacroblockDC(bits, dcLp);
        decodeMacroblockLowPass(bits, dcLp);
        predictionDec.predictDCLPDec(colorFormat, numChannels, dcLp);
        macroblockHp.read(bits, hp);
        predictionDec.predictHPDec(colorFormat, numChannels, hp);
        prediction.updatePredictionInfo(numChannels, dcLp);
        dequantizer.dequantize(dcLp, hp, dqMb);

        for (int ch = 0; ch < numChannels; ch++) {
            int dstBase = (channelOffset + ch) * rowBufsChannelStride
                + currRowBase + currentMacroblockColumn * HdpConstants.MB_SIZE;
            int dqBase = ch * HdpConstants.MB_SIZE;
            for (int k = 0; k < HdpConstants.MB_SIZE; k++) {
                rowBufs[dstBase + k] = dqMb[dqBase + k];
            }
        }
    }

    void decodeMacroblockDC(BitSource bits, short[] dcLp) throws IOException {
        macroblockDc.read(bits, adaptContextHuffman);
        for (int i = 0; i < numChannels; i++) {
            dcLp[i * HdpConstants.DC_LP_SIZE] = macroblockDc.dc()[i];
        }
    }

    void decodeMacroblockLowPass(BitSource bits, short[] dcLp) throws IOException {
        macroblockLp.read(bits, resetAdaptiveScanTotals, adaptContextHuffman);
        for (int i = 0; i < numChannels; i++) {
            System.arraycopy(macroblockLp.lp(), i * HdpConstants.DC_LP_SIZE + 1, dcLp, i * HdpConstants.DC_LP_SIZE + 1, HdpConstants.DC_LP_SIZE - 1);
        }
    }

    void advanceRow() {
        predInfo.advanceRow();
    }
}
