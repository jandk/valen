package be.twofold.valen.core.audio.wave;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;

public record WaveFormatAdpcm(
    short samplesPerBlock,
    short numCoef,
    Shorts coef
) {
    private static final short[] STANDARD_COEFFICIENTS = {
        256, 0, 512, -256, 0, 0, 192, 64, 240, 0, 460, -208, 392, -232
    };

    public WaveFormatAdpcm {
        Check.nonNull(coef, "coef");
        if (coef.length() != numCoef * 2) {
            throw new IllegalArgumentException("numCoef (" + numCoef + ") doesn't match coef length (" + coef.length() + ")");
        }
        if (!Arrays.equals(coef.toArray(), STANDARD_COEFFICIENTS)) {
            throw new UnsupportedOperationException("Unsupported MS ADPCM coefficients, only the standard set is supported");
        }
    }

    public static WaveFormatAdpcm read(WaveFormat format) throws IOException {
        if (format.bitsPerSample() != 4) {
            throw new IOException("Invalid MS ADPCM bits per sample: " + format.bitsPerSample());
        }

        if (format.blockAlign() < 7 * format.channels()) {
            throw new IOException("Invalid MS ADPCM block align: " + format.blockAlign());
        }

        var source = BinarySource.wrap(format.extra());
        var samplesPerBlock = source.readShort();
        var numCoef = source.readShort();
        var coef = source.readShorts(numCoef * 2);

        var expectedSamplesPerBlock = samplesPerBlock(format.blockAlign(), format.channels());
        if (Short.toUnsignedInt(samplesPerBlock) != expectedSamplesPerBlock) {
            throw new IOException("Invalid MS ADPCM samples per block: " + Short.toUnsignedInt(samplesPerBlock) + ", expected " + expectedSamplesPerBlock);
        }

        return new WaveFormatAdpcm(
            samplesPerBlock,
            numCoef,
            coef
        );
    }

    public static int samplesPerBlock(int blockAlign, int channels) {
        return (blockAlign - 7 * channels) * 2 / channels + 2;
    }
}
