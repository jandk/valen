package be.twofold.valen.core.audio.wave;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class WaveReader implements AssetReader.Binary<Audio, Asset> {
    private static final short WAVE_FORMAT_PCM = 0x0001;
    private static final short WAVE_FORMAT_EXTENSIBLE = (short) 0xFFFE;
    private static final long KSDATAFORMAT_BASE_LSB = 0x800000AA00389B71L;

    @Override
    public boolean canRead(Asset asset) {
        return asset.type() == AssetType.AUDIO
            && asset.id().path().extension().equalsIgnoreCase("wav");
    }

    @Override
    public Audio read(BinarySource source, Asset asset, LoadingContext context) throws IOException {
        var waveFile = WaveFile.read(source);
        var format = waveFile.format();

        var formatTag = format.formatTag();
        var channelMask = 0;
        if (format.formatTag() == WAVE_FORMAT_EXTENSIBLE) {
            var extensible = WaveFormatExtensible.read(BinarySource.wrap(format.extra()), format.bitsPerSample());
            formatTag = toFormatTag(extensible.subFormat());
            channelMask = extensible.channelMask();
        }
        if (formatTag != WAVE_FORMAT_PCM) {
            throw new UnsupportedOperationException("Unsupported wave format: " + format.formatTag());
        }

        var data = waveFile.data().payload();
        var codec = toCodec(format.bitsPerSample());
        var sampleRate = format.samplesPerSec();
        var channels = Channel.fromMask(channelMask, format.channels());
        // We don't trust blockAlign
        var blockAlign = format.channels() * (format.bitsPerSample() / 8);
        var frameCount = data.length() / blockAlign;

        return new Audio(
            codec,
            sampleRate,
            channels,
            frameCount,
            data.slice(0, frameCount * blockAlign)
        );
    }

    private short toFormatTag(UUID subFormat) {
        var msb = subFormat.getMostSignificantBits();
        var lsb = subFormat.getLeastSignificantBits();
        if ((msb & 0xFFFFFFFFL) != 0x00000010L || lsb != KSDATAFORMAT_BASE_LSB) {
            throw new UnsupportedOperationException("Unsupported wave subformat: " + subFormat);
        }
        return (short) (msb >>> 32);
    }

    private AudioCodec toCodec(short bitsPerSample) {
        return switch (bitsPerSample) {
            case 8 -> AudioCodec.PCM_U8;
            case 16 -> AudioCodec.PCM_S16_LE;
            default -> throw new UnsupportedOperationException("Unsupported bits per sample: " + bitsPerSample);
        };
    }
}
