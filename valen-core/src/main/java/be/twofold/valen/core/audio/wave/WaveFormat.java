package be.twofold.valen.core.audio.wave;

import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public record WaveFormat(
    short formatTag,
    short channels,
    int samplesPerSec,
    int avgBytesPerSec,
    short blockAlign,
    short bitsPerSample,
    Bytes extra
) {
    public WaveFormat {
        Check.positive(channels, "channels");
        Check.nonNull(extra, "extra");
    }

    public static WaveFormat read(BinarySource source) throws IOException {
        var size = Math.toIntExact(source.remaining());
        if (size < 14) {
            throw new IOException("fmt chunk too small: " + size);
        }
        // WAVEFORMAT
        var formatTag = source.readShort();
        var channels = source.readShort();
        var samplesPerSec = source.readInt();
        var avgBytesPerSec = source.readInt();
        var blockAlign = source.readShort();

        // PCMWAVEFORMAT
        var bitsPerSample = size >= 16 ? source.readShort() : 0;

        // WAVEFORMATEX
        if (size >= 18) {
            source.readShort(); // cbSize, unreliable
        }

        var extra = size > 18 ? source.readBytes(size - 18) : Bytes.empty();

        return new WaveFormat(
            formatTag,
            channels,
            samplesPerSec,
            avgBytesPerSec,
            blockAlign,
            bitsPerSample,
            extra
        );
    }

    public void validatePcm() {
        var bytesPerSample = blockAlign / channels;
        Check.argument(bitsPerSample <= bytesPerSample * 8,
            "bitsPerSample must be less than or equal to bytesPerSample * 8");
        Check.argument(avgBytesPerSec == blockAlign * samplesPerSec,
            "avgBytesPerSec must be equal to blockAlign * samplesPerSec");
        Check.argument(bitsPerSample % 8 == 0,
            "bitsPerSample must be a multiple of 8");
        Check.argument(bitsPerSample != 0,
            "bitsPerSample must be non-zero");
    }
}
