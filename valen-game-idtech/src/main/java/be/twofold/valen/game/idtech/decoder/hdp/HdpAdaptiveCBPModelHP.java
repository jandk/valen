package be.twofold.valen.game.idtech.decoder.hdp;

import java.util.*;

/**
 * Per-channel state machine picking how the HP coded-block-pattern is predicted: spatial, identity, or inverted.
 */
final class HdpAdaptiveCBPModelHP {
    private final int[] countOnes = new int[3];
    private final int[] countZeros = new int[3];
    private final int[] state = new int[3];

    HdpAdaptiveCBPModelHP() {
        reset();
    }

    int state(int ch) {
        return state[ch];
    }

    void update(int ch, int numOnes) {
        countOnes[ch] = Math.clamp(countOnes[ch] + numOnes - 3, -16, 15);
        countZeros[ch] = Math.clamp(countZeros[ch] + 16 - numOnes - 3, -16, 15);
        if (countOnes[ch] < 0) {
            state[ch] = countOnes[ch] < countZeros[ch] ? 1 : 2;
        } else if (countZeros[ch] < 0) {
            state[ch] = 2;
        } else {
            state[ch] = 0;
        }
    }

    void reset() {
        Arrays.fill(countOnes, -4);
        Arrays.fill(countZeros, 4);
        Arrays.fill(state, 0);
    }
}
