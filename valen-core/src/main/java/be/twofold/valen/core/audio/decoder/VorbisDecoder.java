package be.twofold.valen.core.audio.decoder;

import be.twofold.valen.core.audio.*;

public final class VorbisDecoder extends OggDecoder {
    @Override
    public AudioCodec source() {
        return AudioCodec.OGG_VORBIS;
    }
}
