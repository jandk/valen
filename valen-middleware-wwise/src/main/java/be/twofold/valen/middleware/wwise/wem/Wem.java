package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;

public record Wem(
    WaveFormat format,
    int dataOffset,
    int dataSize,
    boolean truncated
) {
    private static final int WAVE = 'W' | 'A' << 8 | 'V' << 16 | 'E' << 24;

    public Wem {
        Check.nonNull(format, "format");
        Check.positive(dataOffset, "dataOffset");
        Check.positive(dataSize, "dataSize");
    }

    public static Wem read(BinarySource source) throws IOException {
        var riff = ChunkHeader.read(source);
        if (riff.tag() != ChunkTag.RIFF) {
            throw new IOException("Expected RIFF chunk");
        }
        source.expectInt(WAVE);

        WaveFormat format = null;

        while (source.remaining() > 0) {
            var chunk = ChunkHeader.read(source);
            var start = source.position();

            switch (chunk.tag()) {
                case fmt_ -> format = WaveFormat.read(source, chunk.size());
                case data -> {
                    return new Wem(
                        format,
                        Math.toIntExact(start),
                        Math.toIntExact(Math.min(source.remaining(), chunk.size())),
                        source.remaining() < chunk.size()
                    );
                }
                default -> source.skip(chunk.size());
            }
        }
        throw new IOException("Expected data chunk");
    }
}
