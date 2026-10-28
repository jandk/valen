package be.twofold.valen.middleware.wwise.wem;

import be.twofold.valen.core.audio.wave.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record WwiseFormat(
    WaveFormat format,
    CodecId codec,
    short samplesPerBlock,
    int channelConfig,
    Optional<CodecHeader> codecHeader
) {
    public static WwiseFormat from(WaveFormat format) throws IOException {
        var codec = CodecId.fromValue(format.formatTag());
        var extra = BinarySource.wrap(format.extra());
        var size = extra.remaining();

        var samplesPerBlock = size >= 6 ? extra.readShort() : 0;
        var channelConfig = size >= 6 ? extra.readInt() : 0;

        CodecHeader codecHeader;
        if (codec == CodecId.VORBIS && size >= 48) {
            codecHeader = AkVorbisHeader.read(extra);
        } else if (codec == CodecId.OPUS && size >= 18) {
            codecHeader = AkOpusHeader.read(extra);
        } else if (codec == CodecId.WEM_OPUS && size >= 18) {
            codecHeader = AkOpusWemHeader.read(extra);
        } else {
            codecHeader = null;
        }

        return new WwiseFormat(
            format,
            codec,
            samplesPerBlock,
            channelConfig,
            Optional.ofNullable(codecHeader)
        );
    }

    public short channels() {
        return format.channels();
    }

    public int samplesPerSec() {
        return format.samplesPerSec();
    }

    public int avgBytesPerSec() {
        return format.avgBytesPerSec();
    }

    public short blockAlign() {
        return format.blockAlign();
    }

    public short bitsPerSample() {
        return format.bitsPerSample();
    }
}
