package be.twofold.valen.game.idtech.decoder.hdp;

/**
 * One slot of an adaptive zigzag scan: the coefficient it points at, and how often that slot was non-zero.
 */
final class HdpAdaptiveScan {
    int scanTotal;
    int scanIndex;

    static HdpAdaptiveScan[] allocate(byte[] order, byte[] permutation) {
        HdpAdaptiveScan[] scans = new HdpAdaptiveScan[16];
        for (int i = 0; i < 16; i++) {
            scans[i] = new HdpAdaptiveScan();
            scans[i].scanIndex = permutation == null ? order[i] : permutation[order[i]];
        }
        resetTotals(scans);
        return scans;
    }

    static void resetTotals(HdpAdaptiveScan[] scans) {
        scans[0].scanTotal = 0x7FFF;
        for (int i = 1; i < 16; i++) {
            scans[i].scanTotal = 32 - 2 * (i - 1);
        }
    }
}
