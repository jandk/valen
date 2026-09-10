package be.twofold.valen.export.wav;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.export.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public final class WavExporter implements Exporter<Audio> {
    private static final byte[] SUBFORMAT = new byte[]{
        0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x10, 0x00,
        (byte) 0x80, 0x00, 0x00, (byte) 0xAA, 0x00, 0x38, (byte) 0x9B, 0x71
    };

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
        var pcmAudio = AudioDecoder.convert(value, AudioCodec.PCM_S16_LE);
        if (pcmAudio.isEmpty()) {
            throw new IllegalArgumentException("Unsupported audio format");
        }

        Bytes fmt = buildFormat(value);
        Bytes pcm = pcmAudio.get().data();
        var wav = new Chunk.Group("RIFF", "WAVE", List.of(
            new Chunk.Data("fmt ", fmt),
            new Chunk.Data("data", pcm),
            new Chunk.Group("LIST", "INFO", List.of(
                new Chunk.Data("ICMT", Bytes.wrap("Exported with Valen\0".getBytes(StandardCharsets.US_ASCII))),
                new Chunk.Data("ISFT", Bytes.wrap("Valen\0".getBytes(StandardCharsets.US_ASCII)))
            ))
        ));

        writeChunk(wav, out);
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
        Bytes.wrap(SUBFORMAT).copyTo(bytes, 24);
    }

    private int toChannelMask(List<Channel> channels) {
        return channels.stream()
            .mapToInt(c -> 1 << c.ordinal())
            .reduce(0, (a, b) -> a | b);
    }

    private void writeChunk(Chunk chunk, OutputStream out) throws IOException {
        out.write(chunk.id().getBytes(StandardCharsets.US_ASCII));
        out.write(Bytes.allocate(4).setInt(0, chunk.size()).toArray());
        switch (chunk) {
            case Chunk.Data data -> {
                try (InputStream in = data.payload().asInputStream()) {
                    in.transferTo(out);
                }
                if ((data.size() & 1) != 0) {
                    out.write(0);
                }
            }
            case Chunk.Group group -> {
                out.write(group.type().getBytes(StandardCharsets.US_ASCII));
                for (Chunk c : group.chunks()) {
                    writeChunk(c, out);
                }
            }
        }
    }

    sealed interface Chunk {
        String id();

        int size();

        record Data(String id, Bytes payload) implements Chunk {
            @Override
            public int size() {
                int length = payload().length();
                return length + (length & 1);
            }
        }

        record Group(String id, String type, List<Chunk> chunks) implements Chunk {
            @Override
            public int size() {
                int size = 4;
                for (Chunk chunk : chunks) {
                    size += 8 + chunk.size();
                }
                return size;
            }
        }
    }
}
