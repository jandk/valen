package be.twofold.valen.middleware.wwise.wem;

import wtf.reversed.toolbox.io.*;

import java.io.*;

public record WaveFormat(
    CodecId codec,
    short channels,
    int samplesPerSec,
    int avgBytesPerSec,
    short blockAlign,
    short bitsPerSample,
    short cbSize,
    short unknown,
    int channelConfig
) {
    public static WaveFormat read(BinarySource source, int size) throws IOException {
        CodecId codec = CodecId.fromValue(source.readShort());
        short channels = source.readShort();
        int samplesPerSec = source.readInt();
        int avgBytesPerSec = source.readInt();
        short blockAlign = source.readShort();
        short bitsPerSample = source.readShort();
        short cbSize = source.readShort();
        short unknown = source.readShort();
        int channelConfig = source.readInt();

        return new WaveFormat(
            codec,
            channels,
            samplesPerSec,
            avgBytesPerSec,
            blockAlign,
            bitsPerSample,
            cbSize,
            unknown,
            channelConfig
        );
    }
}
