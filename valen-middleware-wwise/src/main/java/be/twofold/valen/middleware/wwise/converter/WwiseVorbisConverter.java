package be.twofold.valen.middleware.wwise.converter;

import be.twofold.valen.core.audio.*;
import be.twofold.valen.middleware.wwise.converter.vorbis.*;
import wtf.reversed.toolbox.collect.*;

import java.io.*;

public final class WwiseVorbisConverter implements AudioConverter {
    @Override
    public boolean canDecode(Audio audio, AudioCodec target) {
        return audio.codec() == AudioCodec.WWISE_VORBIS
            && target == AudioCodec.OGG_VORBIS;
    }

    @Override
    public Audio decode(Audio audio, AudioCodec target) {
        try {
            Bytes data = new VorbisConverter(audio).convert();
            return new Audio(
                AudioCodec.OGG_VORBIS,
                audio.sampleRate(),
                audio.channels(),
                audio.frameCount(),
                data
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
