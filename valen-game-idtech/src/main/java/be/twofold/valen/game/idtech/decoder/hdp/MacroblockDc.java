package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

/**
 * DC band decode for one macroblock: one coefficient per channel, plus its fixed-length tail.
 */
final class MacroblockDc {
    private final short[] dcInput = new short[HdpConstants.CHANNELS];
    private final int[] laplacianMean = new int[HdpConstants.CHANNELS];

    private final HdpContext ctx;
    private final HdpColorFormat colorFormat;
    private final int numChannels;

    MacroblockDc(HdpContext ctx, HdpColorFormat colorFormat, int numChannels) {
        this.ctx = ctx;
        this.colorFormat = Check.nonNull(colorFormat, "colorFormat");
        this.numChannels = Check.positive(numChannels, "numChannels");
    }

    short[] dc() {
        return dcInput;
    }

    void read(BitSource bits, boolean resetContext) throws IOException {
        Arrays.fill(dcInput, (short) 0);
        Arrays.fill(laplacianMean, 0);

        switch (colorFormat) {
            case Y_ONLY, N_CHANNEL -> {
                for (int n = 0; n < numChannels; n++) {
                    boolean isDcChFlag = bits.readFlag();
                    dcInput[n] = decodeDc(bits, n, ctx.adaptHuffDC[1], isDcChFlag);
                }
            }
            case YUV_444 -> {
                int isDcYuv = ctx.adaptHuffDC[0].decodeSymbol(bits);
                dcInput[0] = decodeDc(bits, 0, ctx.adaptHuffDC[1], (isDcYuv & 0x04) != 0);
                dcInput[1] = decodeDc(bits, 1, ctx.adaptHuffDC[2], (isDcYuv & 0x02) != 0);
                dcInput[2] = decodeDc(bits, 2, ctx.adaptHuffDC[2], (isDcYuv & 0x01) != 0);
            }
        }

        ctx.adaptCNModelDC.updateAdaptiveCoefficientNormalization(laplacianMean, numChannels);

        if (resetContext) {
            ctx.adaptContextHuffmanDC();
        }
    }

    private short decodeDc(BitSource bits, int channel, HdpAdaptiveHuffman huff, boolean absLevel) throws IOException {
        // DEVIATION: hdpref reads fixedLengthCodeBits[1] when channel is 2; the engine reads the channel's own.
        int iModelBits = ctx.adaptCNModelDC.fixedLengthCodeBits(channel);

        int iQDc = 0;
        if (absLevel) {
            iQDc = RunLevelDecoder.decodeSignificantAbsLevel(bits, huff) - 1;
            // DEVIATION: hdpref accumulates into laplacianMean[1] when channel is 2; the engine uses the channel's own.
            laplacianMean[channel]++;
        }
        if (iModelBits != 0) {
            iQDc = (iQDc << iModelBits) | bits.read(iModelBits);
        }
        if (iQDc != 0 && bits.readFlag()) {
            iQDc = -iQDc;
        }
        return (short) iQDc;
    }
}
