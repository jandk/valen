package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class WemReader implements AssetReader.Binary<Audio, Asset> {
    private static final int WAVE = 'W' | 'A' << 8 | 'V' << 16 | 'E' << 24;

    @Override
    public boolean canRead(Asset asset) {
        return asset instanceof WwiseAsset;
    }

    @Override
    public Audio read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
        var riff = ChunkHeader.read(source);
        if (riff.tag() != ChunkTag.RIFF) {
            throw new IOException("Expected RIFF chunk");
        }
        source.expectInt(WAVE);

        WaveFormat format = null;

        while (source.remaining() > 0) {
            var chunk = ChunkHeader.read(source);
            switch (chunk.tag()) {
                case fmt_ -> format = WaveFormat.read(source, chunk.size());
                case data -> {
                    if (format == null) {
                        throw new IOException("Expected fmt_ chunk");
                    }
                    return new Audio(
                        mapCodec(format.codec()),
                        format.samplesPerSec(),
                        mapChannels(format.channelConfig()),
                        mapFrameCount(format, source.remaining()),
                        source.readBytes(Math.toIntExact(source.remaining()))
                    );
                }
                default -> source.skip(chunk.size());
            }
        }
        throw new IOException("Expected data chunk");
    }

    private AudioCodec mapCodec(CodecId codec) {
        return switch (codec) {
            case PCM -> AudioCodec.PCM_S16_LE;
            case IMA_ADPCM -> AudioCodec.ADPCM_IMA_WWISE;
            case OPUS, WEM_OPUS, PT_ADPCM, PCM_EX, VORBIS ->
                throw new UnsupportedOperationException("Unsupported codec: " + codec);
        };
    }

    private int mapFrameCount(WaveFormat format, long remaining) {
        return switch (format.codec()) {
            case PCM -> (int) (remaining / format.blockAlign());
            case IMA_ADPCM -> 64 * (int) (remaining / format.blockAlign());
            case OPUS, WEM_OPUS, PT_ADPCM, PCM_EX, VORBIS ->
                throw new UnsupportedOperationException("Unsupported codec: " + format.codec());
        };
    }

    private List<Channel> mapChannels(int channelMask) {
        return IntStream.range(0, Channel.VALUES.size())
            .filter(i -> (channelMask & (1 << i)) != 0)
            .mapToObj(Channel.VALUES::get)
            .toList();
    }
}
