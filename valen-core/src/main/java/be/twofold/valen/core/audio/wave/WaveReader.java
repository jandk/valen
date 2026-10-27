package be.twofold.valen.core.audio.wave;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.core.audio.riff.*;
import be.twofold.valen.core.game.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public final class WaveReader implements AssetReader.Binary<Audio, Asset> {
    private static final short WAVE_FORMAT_PCM = 0x0001;
    private static final short WAVE_FORMAT_ADPCM = 0x0002;
    private static final short WAVE_FORMAT_EXTENSIBLE = (short) 0xFFFE;
    private static final long KSDATAFORMAT_BASE_LSB = 0x800000AA00389B71L;

    private static final FourCC FACT = FourCC.parse("fact");

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

        return switch (formatTag) {
            case WAVE_FORMAT_PCM -> readPcm(waveFile, format, channelMask);
            case WAVE_FORMAT_ADPCM -> readMsAdpcm(waveFile, format);
            default -> throw new UnsupportedOperationException("Unsupported wave format: " + formatTag);
        };
    }

    private Audio readPcm(WaveFile waveFile, WaveFormat format, int channelMask) {
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

    private Audio readMsAdpcm(WaveFile waveFile, WaveFormat format) throws IOException {
        int channels = format.channels();
        if (channels > 2) {
            throw new UnsupportedOperationException("Unsupported MS ADPCM channel count: " + channels);
        }

        var adpcm = WaveFormatAdpcm.read(format);
        int blockAlign = format.blockAlign();
        var samplesPerBlock = Short.toUnsignedInt(adpcm.samplesPerBlock());

        var data = waveFile.data().payload();
        var remainder = data.length() % blockAlign;
        var frameCount = data.length() / blockAlign * samplesPerBlock;
        if (remainder >= 7 * channels) {
            frameCount += WaveFormatAdpcm.samplesPerBlock(remainder, channels);
        }

        int fact = waveFile.riff().findFirst(FACT)
            .map(chunk -> chunk.asData().payload())
            .filter(payload -> payload.length() >= 4)
            .map(payload -> payload.getInt(0))
            .orElse(0);

        if (fact > 0 && fact < frameCount) {
            frameCount = fact;
        }

        return new Audio(
            AudioCodec.MS_ADPCM,
            format.samplesPerSec(),
            Channel.fromMask(0, channels),
            frameCount,
            data,
            new AudioCodecInfo.MsAdpcm(blockAlign, samplesPerBlock, adpcm.coef())
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
