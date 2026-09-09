package be.twofold.valen.export.wav;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.export.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public final class WavExporter implements Exporter<Audio> {
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
                new Chunk.Data("ISFT", Bytes.wrap("Exported with Valen\0".getBytes(StandardCharsets.US_ASCII)))
            ))
        ));

        writeChunk(wav, out);
    }

    private Bytes buildFormat(Audio value) {
        return Bytes.allocate(16)
            .setShort(0, (short) 1)
            .setShort(2, (short) value.channels())
            .setInt(4, value.sampleRate())
            .setInt(8, value.sampleRate() * value.channels() * Short.BYTES)
            .setShort(12, (short) (value.channels() * Short.BYTES))
            .setShort(14, (short) 16);
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
