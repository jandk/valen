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

    public static WaveFormat read(BinaryReader reader) throws IOException {
        var size = Math.toIntExact(reader.remaining());
        if (size < 14) {
            throw new IOException("fmt chunk too small: " + size);
        }
        // WAVEFORMAT
        var formatTag = reader.readShort();
        var channels = reader.readShort();
        var samplesPerSec = reader.readInt();
        var avgBytesPerSec = reader.readInt();
        var blockAlign = reader.readShort();

        // PCMWAVEFORMAT
        var bitsPerSample = size >= 16 ? reader.readShort() : 0;

        // WAVEFORMATEX
        if (size >= 18) {
            reader.readShort(); // cbSize, unreliable
        }

        var extra = size > 18 ? reader.readBytes(size - 18) : Bytes.empty();

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
        Check.multipleOf(bitsPerSample, 8, "bitsPerSample");
        Check.argument(bitsPerSample != 0,
            "bitsPerSample must be non-zero");
    }
}
