package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record WaveFormat(
    CodecId codec,
    short channels,
    int samplesPerSec,
    int avgBytesPerSec,
    short blockAlign,
    short bitsPerSample,
    short cbSize,
    short samplesPerBlock,
    int channelConfig,
    Optional<CodecHeader> codecHeader
) {
    public static WaveFormat read(BinarySource source, int size) throws IOException {
        CodecId codec = CodecId.read(source);
        short channels = source.readShort();
        int samplesPerSec = source.readInt();
        int avgBytesPerSec = source.readInt();
        short blockAlign = source.readShort();
        short bitsPerSample = source.readShort();
        short cbSize = source.readShort();
        short samplesPerBlock = source.readShort();
        int channelConfig = source.readInt();

        CodecHeader codecHeader;
        if (codec == CodecId.VORBIS && size >= 66) {
            codecHeader = AkVorbisHeader.read(source);
        } else if (codec == CodecId.OPUS && size >= 36) {
            codecHeader = AkOpusHeader.read(source);
        } else if (codec == CodecId.WEM_OPUS && size >= 36) {
            codecHeader = AkOpusWemHeader.read(source);
        } else {
            codecHeader = null;
        }

        return new WaveFormat(
            codec,
            channels,
            samplesPerSec,
            avgBytesPerSec,
            blockAlign,
            bitsPerSample,
            cbSize,
            samplesPerBlock,
            channelConfig,
            Optional.ofNullable(codecHeader)
        );
    }
}
