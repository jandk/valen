package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.math.*;

import java.util.*;

public record Waveform(
    int channels,
    int buckets,
    int frames,
    int sampleRate,
    float[] min,
    float[] max,
    float[] meanSquare
) {
    private static final int MAX_BUCKETS = 8 * 1024;

    public static Waveform of(Audio audio) {
        if (audio.codec() != AudioCodec.PCM_S16_LE) {
            throw new IllegalArgumentException("Can only summarize PCM_S16_LE audio, got " + audio.codec());
        }

        Bytes data = audio.data();
        int channels = audio.channels();
        int frames = Math.toIntExact(audio.frameCount());

        int perBucket = Math.max(1, Math.ceilDiv(frames, MAX_BUCKETS));
        int numBuckets = Math.max(1, Math.ceilDiv(frames, perBucket));

        float[] min = new float[channels * numBuckets];
        float[] max = new float[channels * numBuckets];
        float[] meanSquare = new float[channels * numBuckets];

        float[] lo = new float[channels];
        float[] hi = new float[channels];
        double[] sum = new double[channels];

        for (int bucket = 0; bucket < numBuckets; bucket++) {
            int start = bucket * perBucket;
            int end = Math.min(start + perBucket, frames);
            if (start >= end) {
                continue;
            }

            Arrays.fill(lo, Float.POSITIVE_INFINITY);
            Arrays.fill(hi, Float.NEGATIVE_INFINITY);
            Arrays.fill(sum, 0.0);

            // Calculate min, max, and mean square
            for (int frame = start; frame < end; frame++) {
                int offset = frame * channels * Short.BYTES;
                for (int channel = 0; channel < channels; channel++) {
                    short sampleS16 = data.getShort(offset + channel * Short.BYTES);
                    float sample = FloatMath.unpackSNorm16(sampleS16);
                    lo[channel] = Math.min(lo[channel], sample);
                    hi[channel] = Math.max(hi[channel], sample);
                    sum[channel] += (double) sample * (double) sample;
                }
            }

            // Move to correct position
            double count = end - start;
            for (int channel = 0; channel < channels; channel++) {
                int offset = channel * numBuckets + bucket;
                min[offset] = lo[channel];
                max[offset] = hi[channel];
                meanSquare[offset] = (float) (sum[channel] / count);
            }
        }

        return new Waveform(
            channels,
            numBuckets,
            frames,
            audio.sampleRate(),
            min,
            max,
            meanSquare
        );
    }
}
