package be.twofold.valen.core.audio;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

/**
 * Represents a piece of audio
 *
 * @param codec       The codec specifying the audio decoding method.
 * @param sampleRate  The number of samples of audio carried per second, expressed in Hz. Must be positive.
 * @param channels    The number of audio channels (e.g., 1 for mono, 2 for stereo). Must be positive.
 * @param sampleCount The total number of audio samples in the data. Must be non-negative.
 * @param data        The raw binary data of the audio stream. Cannot be null.
 */
public record Audio(
    AudioCodec codec,
    int sampleRate,
    int channels,
    long sampleCount,
    Bytes data
) {
    public Audio {
        Check.nonNull(codec, "codec");
        Check.positive(sampleRate, "sampleRate");
        Check.positive(channels, "channels");
        Check.positiveOrZero(sampleCount, "sampleCount");
        Check.nonNull(data, "data");
    }
}
