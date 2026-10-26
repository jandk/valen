package be.twofold.valen.core.audio.wave;

import be.twofold.valen.core.util.*;
import wtf.reversed.toolbox.collect.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;
import java.util.*;

public record WaveFormatExtensible(
    short validBitsPerSample,
    short samplesPerBlock,
    int channelMask,
    UUID subFormat,
    Bytes codecData
) {
    public static WaveFormatExtensible read(BinarySource source, int bitsPerSample) throws IOException {
        if (source.remaining() < 22) {
            throw new IOException("WaveFormatExtensible requires at least 22 bytes");
        }

        short validBitsPerSample;
        short samplesPerBlock;
        if (bitsPerSample != 0) {
            validBitsPerSample = source.readShort();
            samplesPerBlock = 0;
        } else {
            validBitsPerSample = 0;
            samplesPerBlock = source.readShort();
        }
        var channelMask = source.readInt();
        var subFormat = Guids.fromBytes(source.readBytes(16));
        var codecData = source.readBytes(Math.toIntExact(source.remaining()));

        return new WaveFormatExtensible(
            validBitsPerSample,
            samplesPerBlock,
            channelMask,
            subFormat,
            codecData
        );
    }
}
