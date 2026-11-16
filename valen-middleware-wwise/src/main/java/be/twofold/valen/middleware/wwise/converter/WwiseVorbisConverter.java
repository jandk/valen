package be.twofold.valen.middleware.wwise.converter;

import be.twofold.valen.core.audio.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;

public final class WwiseVorbisConverter implements AudioConverter {
    @Override
    public AudioCodec source() {
        return AudioCodec.WWISE_VORBIS;
    }

    @Override
    public AudioCodec target() {
        return AudioCodec.OGG_VORBIS;
    }

    @Override
    public Audio convert(Audio audio) throws IOException {
        Bytes data = new VorbisConverter(audio).convert();
        return new Audio(
            AudioCodec.OGG_VORBIS,
            audio.sampleRate(),
            audio.channels(),
            audio.frameCount(),
            data
        );
    }
}
