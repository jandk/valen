package be.twofold.valen.middleware.wwise.converter;

import be.twofold.valen.core.audio.*;

public final class WwiseVorbisConverter implements AudioConverter {
    @Override
    public boolean canDecode(Audio audio, AudioCodec target) {
        // return audio.codec() == AudioCodec.WWISE_VORBIS
        //     && target == AudioCodec.OGG_VORBIS;
        return false;
    }

    @Override
    public Audio decode(Audio audio, AudioCodec target) {
        throw new UnsupportedOperationException();
    }
}
