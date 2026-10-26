package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.audio.riff.*;
import be.twofold.valen.core.audio.wave.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.util.*;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public record Wem(
    WwiseFormat format,
    RiffChunk.Data data,
    Optional<String> label,
    Optional<Shorts> seek
) {
    private static final FourCC LABL = FourCC.parse("labl");
    private static final FourCC LIST = FourCC.parse("LIST");
    private static final FourCC SEEK = FourCC.parse("seek");

    public Wem {
        Check.nonNull(format, "format");
        Check.nonNull(data, "data");
    }

    public static Wem read(BinarySource source) throws IOException {
        var waveFile = WaveFile.read(source);
        var format = WwiseFormat.from(waveFile.format());
        var seek = waveFile.riff().findFirst(SEEK)
            .map(c -> c.asData().payload().asShorts());
        return new Wem(format, waveFile.data(), findLabel(waveFile.riff()), seek);
    }

    private static Optional<String> findLabel(RiffChunk.Container riff) {
        for (var chunk : riff.chunks()) {
            if (!chunk.id().equals(LIST)) {
                continue;
            }
            var labl = chunk.asContainer().findFirst(LABL);
            if (labl.isEmpty()) {
                continue;
            }
            var payload = labl.get().asData().payload();
            if (payload.length() < 4) {
                continue;
            }
            var text = payload.slice(4).toString(StandardCharsets.ISO_8859_1);
            var end = text.indexOf('\0');
            return Optional.of(end < 0 ? text : text.substring(0, end));
        }
        return Optional.empty();
    }

    public AudioCodec toAudioCodec() {
        return switch (format.codec()) {
            case PCM, PCM_EX -> AudioCodec.PCM_S16_LE;
            case IMA_ADPCM -> AudioCodec.WWISE_IMA_ADPCM;
            case PT_ADPCM -> AudioCodec.WWISE_PTADPCM;
            case VORBIS -> AudioCodec.WWISE_VORBIS;
            case WEM_OPUS -> AudioCodec.WWISE_OPUS;
            case OPUS -> throw new UnsupportedOperationException("Unsupported codec: " + format.codec());
        };
    }

    public List<Channel> toChannels() {
        var channelConfig = format.channelConfig();
        int channels = format.channels();

        var type = (channelConfig >>> 8) & 0x0F;
        var mask = type == 1 && Integer.bitCount(channelConfig >>> 12) == channels
            ? channelConfig >>> 12
            : channelConfig;
        return Channel.fromMask(mask, channels);
    }

    public int toFrameCount() {
        return switch (format.codec()) {
            case PCM, PCM_EX -> data.payload().length() / format.blockAlign();
            case IMA_ADPCM, PT_ADPCM -> 64 * (data.payload().length() / format.blockAlign());
            case VORBIS -> ((AkVorbisHeader) format.codecHeader().orElseThrow()).totalPCMFrames();
            case WEM_OPUS -> ((AkOpusWemHeader) format.codecHeader().orElseThrow()).sampleCount();
            case OPUS -> throw new UnsupportedOperationException("Unsupported codec: " + format.codec());
        };
    }
}
