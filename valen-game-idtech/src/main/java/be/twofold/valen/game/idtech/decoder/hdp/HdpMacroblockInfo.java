package be.twofold.valen.game.idtech.decoder.hdp;

/**
 * Per-macroblock decode results handed between the band decoders, the predictors and the dequantizer.
 */
final class HdpMacroblockInfo {
    final int[] actualCBP;
    final int[] residualCBP;
    final int[] predOrientationHP;

    // Always 0 for id-Tech, which quantizes uniformly per tile.
    int quantizerIndexLP;

    HdpMacroblockInfo(int numChannels) {
        this.actualCBP = new int[numChannels];
        this.residualCBP = new int[numChannels];
        this.predOrientationHP = new int[numChannels];
    }
}
