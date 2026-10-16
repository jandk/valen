package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public record Wem(
    WaveFormat format,
    int dataOffset,
    int dataSize,
    int declaredDataSize
) {
    private static final int WAVE = 'W' | 'A' << 8 | 'V' << 16 | 'E' << 24;

    public Wem {
        Check.nonNull(format, "format");
        Check.positive(dataOffset, "dataOffset");
        Check.positiveOrZero(dataSize, "dataSize");
        Check.positiveOrZero(declaredDataSize, "declaredDataSize");
    }

    public static Wem read(BinarySource source) throws IOException {
        ChunkHeader riff = ChunkHeader.read(source);
        if (riff.tag() != ChunkTag.RIFF) {
            throw new IOException("Expected RIFF chunk");
        }
        source.expectInt(WAVE);

        WaveFormat format = null;

        while (source.remaining() > 0) {
            ChunkHeader chunk = ChunkHeader.read(source);
            int start = Math.toIntExact(source.position());

            switch (chunk.tag()) {
                case fmt_ -> format = WaveFormat.read(source, chunk.size());
                case data -> {
                    if (format == null) {
                        throw new IOException("Expected fmt_ chunk");
                    }

                    return new Wem(
                        format,
                        start,
                        Math.min(Math.toIntExact(source.remaining()), chunk.size()),
                        chunk.size()
                    );
                }
                default -> {
                }
            }
            source.position(start + chunk.size());
        }
        throw new IOException("Expected data chunk");
    }
}
