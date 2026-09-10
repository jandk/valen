package be.twofold.valen.core.audio;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

/**
 * Represents a piece of audio
 *
 * @param codec      The codec specifying the audio decoding method.
 * @param sampleRate The number of samples of audio carried per second, expressed in Hz. Must be positive.
 * @param channels   The number of audio channels (e.g., 1 for mono, 2 for stereo). Must be positive.
 * @param frameCount The total number of audio frames in the data. Must be non-negative.
 * @param data       The raw binary data of the audio stream. Cannot be null.
 */
public record Audio(
    AudioCodec codec,
    int sampleRate,
    List<Channel> channels,
    int frameCount,
    Bytes data
) {
    public Audio {
        Check.nonNull(codec, "codec");
        Check.positive(sampleRate, "sampleRate");
        Check.positive(channels.size(), "channels is empty");
        Check.positiveOrZero(frameCount, "frameCount");
        Check.nonNull(data, "data");
    }

    public float duration() {
        return (float) frameCount / (float) sampleRate;
    }
}
