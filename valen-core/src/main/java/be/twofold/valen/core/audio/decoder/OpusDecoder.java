package be.twofold.valen.core.audio.decoder;

import be.twofold.valen.core.audio.*;

public final class OpusDecoder extends OggDecoder {
    @Override
    public AudioCodec source() {
        return AudioCodec.OGG_OPUS;
    }
}
