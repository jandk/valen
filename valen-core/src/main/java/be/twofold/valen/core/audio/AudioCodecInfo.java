package be.twofold.valen.core.audio;

import wtf.reversed.toolbox.collect.*;

public sealed interface AudioCodecInfo {
    record None() implements AudioCodecInfo {
    }

    record MsAdpcm(
        int blockAlign,
        int samplesPerBlock,
        Shorts coefficients
    ) implements AudioCodecInfo {
    }
}
