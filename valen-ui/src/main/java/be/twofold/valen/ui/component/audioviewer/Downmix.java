package be.twofold.valen.ui.component.audioviewer;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;

import static be.twofold.valen.core.audio.Channel.*;

final class Downmix {
    private static final float UNIT = 1.0f;

    // -3dB ladder
    private static final float M3DB = 0.7071067811865476f; // Math.pow(2, -0.5)
    private static final float M6DB = 0.5f;                // Math.pow(2, -1.0)
    private static final float M9DB = 0.3535533905932738f; // Math.pow(2, -1.5)

    // equal spread
    private static final float S1_2 = 0.7071067811865476f; // Math.sqrt(1.0 / 2.0)

    private static final float[] TO_MONO = {
        M3DB, // FRONT_LEFT
        M3DB, // FRONT_RIGHT
        UNIT, // FRONT_CENTER
        0.0f, // LOW_FREQUENCY, not mixed
        M6DB, // BACK_LEFT
        M6DB, // BACK_RIGHT
        0.0f, // FRONT_LEFT_OF_CENTER, unused
        0.0f, // FRONT_RIGHT_OF_CENTER, unused
        M6DB, // BACK_CENTER
        M6DB, // SIDE_LEFT
        M6DB, // SIDE_RIGHT
        UNIT, // TOP_CENTER
        M6DB, // TOP_FRONT_LEFT
        M3DB, // TOP_FRONT_CENTER
        M6DB, // TOP_FRONT_RIGHT
        M9DB, // TOP_BACK_LEFT
        0.0f, // TOP_BACK_CENTER
        M9DB, // TOP_BACK_RIGHT
    };

    private static final float[] TO_STEREO = {
        UNIT, 0.0f, // FRONT_LEFT
        0.0f, UNIT, // FRONT_RIGHT
        M3DB, M3DB, // FRONT_CENTER
        0.0f, 0.0f, // LOW_FREQUENCY, not mixed
        M3DB, 0.0f, // BACK_LEFT
        0.0f, M3DB, // BACK_RIGHT
        0.0f, 0.0f, // FRONT_LEFT_OF_CENTER, unused
        0.0f, 0.0f, // FRONT_RIGHT_OF_CENTER, unused
        M3DB, M3DB, // BACK_CENTER
        M3DB, 0.0f, // SIDE_LEFT
        0.0f, M3DB, // SIDE_RIGHT
        S1_2, S1_2, // TOP_CENTER
        M3DB, 0.0f, // TOP_FRONT_LEFT
        M6DB, M6DB, // TOP_FRONT_CENTER
        0.0f, M3DB, // TOP_FRONT_RIGHT
        M6DB, 0.0f, // TOP_BACK_LEFT
        0.0f, 0.0f, // TOP_BACK_CENTER
        0.0f, M6DB, // TOP_BACK_RIGHT
    };

    static Audio downmix(Audio source, int channels) {
        if (source.codec() != AudioCodec.PCM_S16_LE) {
            throw new UnsupportedOperationException("Unsupported codec: " + source.codec());
        }

        int[] indices = source.channels().stream()
            .mapToInt(Channel::ordinal)
            .toArray();

        var target = Bytes.allocate(source.frameCount() * channels * Short.BYTES);
        switch (channels) {
            case 1 -> downmixMono(source, target, indices);
            case 2 -> downmixStereo(source, target, indices);
            default -> throw new UnsupportedOperationException("Unsupported number of channels: " + channels);
        }

        return new Audio(
            source.codec(),
            source.sampleRate(),
            channels == 1 ? MONO : STEREO,
            source.frameCount(),
            target
        );
    }

    private static void downmixMono(Audio src, Bytes.Mutable dst, int[] channels) {
        var source = src.data().asShorts();
        var target = dst.asShorts();
        int channelCount = channels.length;
        for (int i = 0, lim = src.frameCount(); i < lim; i++) {
            float sum = 0.0f;
            for (int c = 0; c < channelCount; c++) {
                int sample = source.get(i * channelCount + c);
                sum += sample * TO_MONO[channels[c]];
            }
            target.set(i, (short) packSample(sum));
        }
    }

    private static void downmixStereo(Audio src, Bytes.Mutable dst, int[] channels) {
        var source = src.data().asShorts();
        var target = dst.asShorts();
        int channelCount = channels.length;
        for (int i = 0, lim = src.frameCount(); i < lim; i++) {
            float sumL = 0.0f;
            float sumR = 0.0f;
            for (int c = 0; c < channelCount; c++) {
                int sample = source.get(i * channelCount + c);
                sumL += sample * TO_STEREO[channels[c] * 2/**/];
                sumR += sample * TO_STEREO[channels[c] * 2 + 1];
            }
            target.set(i * 2/**/, (short) packSample(sumL));
            target.set(i * 2 + 1, (short) packSample(sumR));
        }
    }

    private static int packSample(float sample) {
        int s = (int) (sample + Math.copySign(0.5f, sample));
        return Math.clamp(s, Short.MIN_VALUE, Short.MAX_VALUE);
    }
}
