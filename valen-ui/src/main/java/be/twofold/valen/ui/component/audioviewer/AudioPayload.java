package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.util.*;

public record AudioPayload(
    Audio pcmAudio,
    Waveform waveform,
    String status,
    String message
) {
    public AudioPayload {
        Check.nonNull(pcmAudio, "pcmAudio");
        Check.nonNull(waveform, "waveform");
        Check.nonNull(status, "status");
    }
}
