package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public record Wem(
    WaveFormat format,
    int dataOffset,
    int dataSize,
    int declaredDataSize,
    Optional<String> label,
    Optional<Shorts> seek
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
        String label = null;
        Shorts seek = null;

        while (source.remaining() > 0) {
            ChunkHeader chunk = ChunkHeader.read(source);
            int start = Math.toIntExact(source.position());

            switch (chunk.tag()) {
                case fmt_ -> format = WaveFormat.read(source, chunk.size());
                case LIST -> {
                    source.skip(4); // type
                    continue;
                }
                case labl -> {
                    if (label == null) {
                        source.skip(4); // cueId
                        var text = source.readString(chunk.size() - 4);
                        int end = text.indexOf('\0');
                        label = end < 0 ? text : text.substring(0, end);
                    }
                }
                case seek -> seek = source.readShorts(chunk.size() / 2);
                case data -> {
                    if (format == null) {
                        throw new IOException("Expected fmt_ chunk");
                    }

                    return new Wem(
                        format,
                        start,
                        Math.min(Math.toIntExact(source.remaining()), chunk.size()),
                        chunk.size(),
                        Optional.ofNullable(label),
                        Optional.ofNullable(seek)
                    );
                }
                default -> {
                }
            }
            source.position(start + chunk.size() + (chunk.size() & 1));
        }
        throw new IOException("Expected data chunk");
    }

    public AudioCodec toAudioCodec() {
        return switch (format.codec()) {
            case PCM, PCM_EX -> AudioCodec.PCM_S16_LE;
            case IMA_ADPCM -> AudioCodec.WWISE_IMA_ADPCM;
            case VORBIS -> AudioCodec.WWISE_VORBIS;
            case OPUS, WEM_OPUS, PT_ADPCM ->
                throw new UnsupportedOperationException("Unsupported codec: " + format.codec());
        };
    }

    public List<Channel> toChannels() {
        int channelConfig = format.channelConfig();
        int channels = format.channels();

        int type = (channelConfig >>> 8) & 0x0F;
        int mask = type == 1 && Integer.bitCount(channelConfig >>> 12) == channels
            ? channelConfig >>> 12
            : channelConfig;
        if (Integer.bitCount(mask) != channels) {
            throw new UnsupportedOperationException("Unsupported channel mask: 0x" + Integer.toHexString(channelConfig));
        }
        return IntStream.range(0, Channel.VALUES.size())
            .filter(i -> (mask & (1 << i)) != 0)
            .mapToObj(Channel.VALUES::get)
            .toList();
    }

    public int toFrameCount() {
        return switch (format.codec()) {
            case PCM, PCM_EX -> dataSize / format.blockAlign();
            case IMA_ADPCM -> 64 * (dataSize / format.blockAlign());
            case VORBIS -> ((AkVorbisHeader) format.codecHeader().orElseThrow()).totalPCMFrames();
            case OPUS, WEM_OPUS, PT_ADPCM ->
                throw new UnsupportedOperationException("Unsupported codec: " + format.codec());
        };
    }
}
