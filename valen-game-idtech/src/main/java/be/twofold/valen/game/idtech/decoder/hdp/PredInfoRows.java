package be.twofold.valen.game.idtech.decoder.hdp;

/**
 * Two-MB-row circulant of {@link HdpPredictionInfo}, exposing the current macroblock's spatial neighbours.
 */
final class PredInfoRows {
    private HdpPredictionInfo[][] currentRow;
    private HdpPredictionInfo[][] previousRow;

    PredInfoRows(int numChannels, int mbWidth) {
        this.currentRow = newRow(numChannels, mbWidth);
        this.previousRow = newRow(numChannels, mbWidth);
    }

    HdpPredictionInfo current(int ch, int mbX) {
        return currentRow[ch][mbX];
    }

    HdpPredictionInfo left(int ch, int mbX) {
        return currentRow[ch][mbX - 1];
    }

    HdpPredictionInfo top(int ch, int mbX) {
        return previousRow[ch][mbX];
    }

    HdpPredictionInfo topLeft(int ch, int mbX) {
        return previousRow[ch][mbX - 1];
    }

    void advanceRow() {
        var tmp = previousRow;
        previousRow = currentRow;
        currentRow = tmp;
        for (var row : currentRow) {
            for (var pi : row) {
                pi.reset();
            }
        }
    }

    private HdpPredictionInfo[][] newRow(int numChannels, int mbWidth) {
        var row = new HdpPredictionInfo[numChannels][mbWidth];
        for (int ch = 0; ch < numChannels; ch++) {
            for (int x = 0; x < mbWidth; x++) {
                row[ch][x] = new HdpPredictionInfo();
            }
        }
        return row;
    }
}
