package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.util.*;

public record AudioPayload(
    Audio pcmAudio,
    Waveform waveform,
    String status,
    String message,
    float duration
) {
    public AudioPayload {
        Check.nonNull(status, "status");
    }

    public static AudioPayload decoded(Audio audio, String status) {
        return new AudioPayload(audio, Waveform.of(audio), status, null, audio.duration());
    }

    public static AudioPayload undecoded(Audio audio, String status) {
        return new AudioPayload(null, null, status, "No decoder for " + audio.codec(), audio.duration());
    }
}
