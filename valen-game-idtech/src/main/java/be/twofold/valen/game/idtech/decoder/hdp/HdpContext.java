package be.twofold.valen.game.idtech.decoder.hdp;

import be.twofold.valen.game.idtech.decoder.hdp.header.*;

/**
 * All adaptive entropy state for one plane decode: the per-band Huffman table sets, zigzag scans and coding models.
 */
final class HdpContext {
    private static final byte[] coeffOrderHP = {
        0, 5, 1, 6, 10, 12, 8, 14, 2, 4, 3, 7, 9, 13, 11, 15
    };
    private static final byte[] grgiZigzagInv4x4_lowpass = {
        0, 1, 4, 5, 2, 8, 6, 9, 3, 12, 10, 7, 13, 11, 14, 15
    };
    private static final byte[] grgiZigzagInv4x4H_highpass = {
        0, 1, 4, 5, 2, 8, 6, 9, 3, 12, 10, 7, 13, 11, 14, 15
    };
    private static final byte[] grgiZigzagInv4x4V_highpass = {
        0, 4, 8, 5, 1, 12, 9, 6, 2, 13, 3, 15, 7, 10, 14, 11
    };

    final HdpAdaptiveHuffman[] adaptHuffDC = HdpAdaptiveHuffman.allocate(8, 7, 7);
    final HdpAdaptiveHuffman[] adaptHuffLP = HdpAdaptiveHuffman.allocate(12, 6, 6, 12, 6, 6, 7, 7);
    final HdpAdaptiveHuffman[] adaptHuffHP = HdpAdaptiveHuffman.allocate(12, 6, 6, 12, 6, 6, 7, 7);
    final HdpAdaptiveHuffman[] adaptHuffCBP;

    final HdpAdaptiveScan[] adaptScanLP = HdpAdaptiveScan.allocate(grgiZigzagInv4x4_lowpass, null);
    final HdpAdaptiveScan[] adaptScanHorHP = HdpAdaptiveScan.allocate(grgiZigzagInv4x4H_highpass, coeffOrderHP);
    final HdpAdaptiveScan[] adaptScanVerHP = HdpAdaptiveScan.allocate(grgiZigzagInv4x4V_highpass, coeffOrderHP);

    final HdpAdaptiveCNModel adaptCNModelDC = new HdpAdaptiveCNModel(HdpBand.BAND_DC, 8);
    final HdpAdaptiveCNModel adaptCNModelLP = new HdpAdaptiveCNModel(HdpBand.BAND_LP, 4);
    final HdpAdaptiveCNModel adaptCNModelHP = new HdpAdaptiveCNModel(HdpBand.BAND_HP, 0);

    final HdpAdaptiveCBPModelLP adaptCBPModelLP = new HdpAdaptiveCBPModelLP();
    final HdpAdaptiveCBPModelHP adaptCBPModelHP = new HdpAdaptiveCBPModelHP();

    HdpContext(HdpColorFormat colorFormat) {
        boolean small = colorFormat == HdpColorFormat.Y_ONLY
            || colorFormat == HdpColorFormat.CMYK
            || colorFormat == HdpColorFormat.N_CHANNEL;
        adaptHuffCBP = HdpAdaptiveHuffman.allocate(small ? 5 : 9, 5, 4);
    }

    void adaptContextHuffmanDC() {
        for (var adaptHuff : adaptHuffDC) {
            adaptHuff.adaptDiscriminant();
        }
    }

    void adaptContextHuffmanLP() {
        for (var adaptHuff : adaptHuffLP) {
            adaptHuff.adaptDiscriminant();
        }
    }

    void adaptContextHuffmanHP() {
        for (var adaptHuff : adaptHuffHP) {
            adaptHuff.adaptDiscriminant();
        }
        for (var adaptHuff : adaptHuffCBP) {
            adaptHuff.adaptDiscriminant();
        }
    }
}
