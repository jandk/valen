package be.twofold.valen.core.audio.export;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.audio.riff.*;
import be.twofold.valen.core.export.*;
import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public final class WaveExporter implements Exporter<Audio> {
    private static final UUID KSDATAFORMAT_SUBTYPE_PCM = UUID.fromString("00000001-0000-0010-8000-00AA00389B71");

    @Override
    public String getID() {
        return "audio.wav";
    }

    @Override
    public String getName() {
        return "WAV Audio";
    }

    @Override
    public String getExtension() {
        return "wav";
    }

    @Override
    public Class<Audio> getSupportedType() {
        return Audio.class;
    }

    @Override
    public void export(Audio value, OutputStream out) throws IOException {
        var pcmAudio = AudioDecoder.toPcm(value);
        if (pcmAudio.isEmpty()) {
            throw new IllegalArgumentException("Unsupported audio format");
        }

        Bytes fmt = buildFormat(value);
        Bytes pcm = pcmAudio.get().data();
        RiffChunk.container("RIFF", "WAVE",
            RiffChunk.data("fmt ", fmt),
            RiffChunk.data("data", pcm),
            RiffChunk.container("LIST", "INFO",
                RiffChunk.data("ICMT", Bytes.wrap(("Exported with Valen " + Valen.version() + "\0").getBytes(StandardCharsets.US_ASCII))),
                RiffChunk.data("ISFT", Bytes.wrap((Valen.software() + "\0").getBytes(StandardCharsets.US_ASCII)))
            )
        ).write(out);
    }

    private Bytes buildFormat(Audio audio) {
        boolean extendedHeader = !audio.channels().equals(Channel.MONO)
            && !audio.channels().equals(Channel.STEREO);

        Bytes.Mutable format = Bytes.allocate(extendedHeader ? 40 : 16);
        standardHeader(audio, format, extendedHeader);
        if (extendedHeader) {
            extendedHeader(audio, format);
        }
        return format;
    }

    private void standardHeader(Audio audio, Bytes.Mutable bytes, boolean extendedHeader) {
        bytes
            .setShort(0, (short) (extendedHeader ? 0xFFFE : 0x0001))
            .setShort(2, (short) audio.channels().size())
            .setInt(4, audio.sampleRate())
            .setInt(8, audio.sampleRate() * audio.channels().size() * Short.BYTES)
            .setShort(12, (short) (audio.channels().size() * Short.BYTES))
            .setShort(14, (short) 16);
    }

    private void extendedHeader(Audio audio, Bytes.Mutable bytes) {
        bytes
            .setShort(16, (short) 22)
            .setShort(18, (short) 16)
            .setInt(20, toChannelMask(audio.channels()));
        Guids.toBytes(KSDATAFORMAT_SUBTYPE_PCM).copyTo(bytes, 24);
    }

    private int toChannelMask(List<Channel> channels) {
        return channels.stream()
            .mapToInt(c -> 1 << c.ordinal())
            .reduce(0, (a, b) -> a | b);
    }
}
