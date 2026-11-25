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
    public static WaveFormatExtensible read(BinaryReader reader, int bitsPerSample) throws IOException {
        if (reader.remaining() < 22) {
            throw new IOException("WaveFormatExtensible requires at least 22 bytes");
        }

        short validBitsPerSample;
        short samplesPerBlock;
        if (bitsPerSample != 0) {
            validBitsPerSample = reader.readShort();
            samplesPerBlock = 0;
        } else {
            validBitsPerSample = 0;
            samplesPerBlock = reader.readShort();
        }
        var channelMask = reader.readInt();
        var subFormat = Guids.fromBytes(reader.readBytes(16));
        var codecData = reader.readRemaining();

        return new WaveFormatExtensible(
            validBitsPerSample,
            samplesPerBlock,
            channelMask,
            subFormat,
            codecData
        );
    }
}
