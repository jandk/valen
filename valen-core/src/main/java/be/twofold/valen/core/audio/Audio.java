package be.twofold.valen.core.audio;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.util.*;

import java.util.*;

/**
 * Represents a piece of audio
 *
 * @param codec      The codec specifying the audio decoding method.
 * @param sampleRate The number of samples of audio carried per second, expressed in Hz. Must be positive.
 * @param channels   The speaker of each channel, in the order the channels appear in the data. Must not be empty.
 * @param frameCount The total number of audio frames in the data. Must be non-negative.
 * @param data       The raw binary data of the audio stream. Cannot be null.
 */
public record Audio(
    AudioCodec codec,
    int sampleRate,
    List<AudioChannel> channels,
    int frameCount,
    Bytes data,
    AudioCodecInfo info
) {
    public Audio {
        Check.nonNull(codec, "codec");
        Check.positive(sampleRate, "sampleRate");
        Check.nonEmpty(channels, "channels");
        Check.nonNegative(frameCount, "frameCount");
        Check.nonNull(data, "data");
    }

    public Audio(AudioCodec codec, int sampleRate, List<AudioChannel> channels, int frameCount, Bytes data) {
        this(codec, sampleRate, channels, frameCount, data, new AudioCodecInfo.None());
    }

    public float duration() {
        return (float) frameCount / (float) sampleRate;
    }
}
