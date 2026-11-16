package be.twofold.valen.core.audio.wave;

import be.twofold.valen.core.audio.riff.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record WaveFile(
    RiffChunk.Container riff,
    WaveFormat format,
    RiffChunk.Data data
) {
    private static final FourCC RIFF = FourCC.parse("RIFF");
    private static final FourCC WAVE = FourCC.parse("WAVE");

    private static final RiffReader RIFF_READER = new RiffReader();

    public static WaveFile read(BinarySource source) throws IOException {
        var riff = RIFF_READER.read(source);
        if (!riff.id().equals(RIFF) || !riff.type().equals(WAVE)) {
            throw new IOException("Expected RIFF/WAVE format");
        }

        var fmt = riff.findFirst(FourCC.parse("fmt "))
            .orElseThrow(() -> new IOException("Expected fmt chunk"))
            .asData();
        var format = WaveFormat.read(BinarySource.wrap(fmt.payload()));

        var data = riff.findFirst(FourCC.parse("data"))
            .orElseThrow(() -> new IOException("Expected data chunk"))
            .asData();

        return new WaveFile(
            riff,
            format,
            data
        );
    }
}
