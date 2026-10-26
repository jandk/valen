package be.twofold.valen.core.audio.export;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.export.*;
import be.twofold.valen.core.util.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class NativeAudioExporter implements Exporter<Audio> {
    private static final List<AudioCodec> TARGETS = List.of(
        AudioCodec.OGG_OPUS,
        AudioCodec.OGG_VORBIS,
        AudioCodec.PCM_S16_LE
    );

    @Override
    public String getID() {
        return "audio.native";
    }

    @Override
    public String getName() {
        return "Native format (WAV fallback)";
    }

    @Override
    public String getExtension() {
        return "";
    }

    @Override
    public Class<Audio> getSupportedType() {
        return Audio.class;
    }

    @Override
    public void export(Audio value, OutputStream out) throws IOException {
        // TODO: Eehm, Liskov fail for now
        throw new UnsupportedOperationException("Native Audio can't export to a stream");
    }

    @Override
    public void export(Audio value, Path path) throws IOException {
        for (AudioCodec target : TARGETS) {
            var converted = AudioConverter.convert(value, target);
            if (converted.isPresent()) {
                write(converted.get(), path);
                return;
            }
        }

        throw new IOException("No conversion for " + value.codec());
    }

    private static void write(Audio converted, Path path) throws IOException {
        String extension = switch (converted.codec()) {
            case OGG_OPUS -> "opus";
            case OGG_VORBIS -> "ogg";
            case PCM_S16_LE -> "wav";
            default -> throw new AssertionError();
        };

        Path outputPath = PathUtils.replaceExtension(path, extension);

        switch (converted.codec()) {
            case OGG_OPUS:
            case OGG_VORBIS:
                try (var in = converted.data().asInputStream();
                     var out = Files.newOutputStream(outputPath)) {
                    in.transferTo(out);
                }
                break;
            case PCM_S16_LE:
                new WaveExporter().export(converted, outputPath);
                break;
            default:
                throw new AssertionError();
        }
    }
}
