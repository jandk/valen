package be.twofold.valen.game.idtech.decoder.hdp;

/**
 * Scales one macroblock's entropy-decoded coefficients by the plane's quantizers, and scatters.
 */
final class HdpQuantizationDec {
    private static final int[] dctIndex = {
        0, 128, 64, 208, 32, 240, 48, 224,
        16, 192, 80, 144, 112, 176, 96, 160
    };

    private final HdpTileQuantization qp;

    HdpQuantizationDec(HdpTileQuantization qp) {
        this.qp = qp;
    }

    // DEVIATION: the engine keeps DC+LP and HP in two buffers and reads both as one; they are merged here.
    void dequantize(short[] dcLp, short[] hp, short[] out) {
        for (int ch = 0; ch < qp.numChannels(); ch++) {
            int dcLpBase = ch * HdpConstants.DC_LP_SIZE;
            int mbBase = ch * HdpConstants.MB_SIZE;

            // The (short) casts mirror the engine's _mm_mullo_epi16, which keeps only the low 16 bits.
            int hpDivisor = qp.quantizerHP()[ch];
            for (int i = 0; i < HdpConstants.MB_SIZE; i++) {
                out[mbBase + i] = (short) (hp[mbBase + i] * hpDivisor);
            }

            int lpDivisor = qp.quantizerLP()[ch];
            for (int i = 1; i < HdpConstants.DC_LP_SIZE; i++) {
                out[mbBase + dctIndex[i]] = (short) (dcLp[dcLpBase + i] * lpDivisor);
            }

            out[mbBase] = (short) (dcLp[dcLpBase] * qp.quantizerDC()[ch]);
        }
    }
}
