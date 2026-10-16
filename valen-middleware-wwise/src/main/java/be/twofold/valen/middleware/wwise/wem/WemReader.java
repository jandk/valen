package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.game.*;
import be.twofold.valen.middleware.wwise.shared.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public final class WemReader implements AssetReader.Binary<Audio, Asset> {
    @Override
    public boolean canRead(Asset asset) {
        return asset instanceof WwiseAsset;
    }

    @Override
    public Audio read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
        var wem = Wem.read(source);
        var format = wem.format();
        source.position(wem.dataOffset());

        return new Audio(
            mapCodec(format.codec()),
            format.samplesPerSec(),
            mapChannels(format.channelConfig(), format.channels()),
            mapFrameCount(format, wem.dataSize()),
            source.readBytes(wem.dataSize())
        );
    }

    private AudioCodec mapCodec(CodecId codec) {
        return switch (codec) {
            case PCM, PCM_EX -> AudioCodec.PCM_S16_LE;
            case IMA_ADPCM -> AudioCodec.WWISE_IMA_ADPCM;
            case VORBIS -> AudioCodec.WWISE_VORBIS;
            case OPUS, WEM_OPUS, PT_ADPCM -> throw new UnsupportedOperationException("Unsupported codec: " + codec);
        };
    }

    private int mapFrameCount(WaveFormat format, long remaining) {
        return switch (format.codec()) {
            case PCM, PCM_EX -> (int) (remaining / format.blockAlign());
            case IMA_ADPCM -> 64 * (int) (remaining / format.blockAlign());
            case VORBIS -> ((AkVorbisHeader) format.codecHeader().orElseThrow()).totalPCMFrames();
            case OPUS, WEM_OPUS, PT_ADPCM ->
                throw new UnsupportedOperationException("Unsupported codec: " + format.codec());
        };
    }

    private List<Channel> mapChannels(int channelConfig, int channels) {
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
}
