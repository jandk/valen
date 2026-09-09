package be.twofold.valen.middleware.wwise.codec;

import be.twofold.valen.core.audio.*;

public final class WwiseImaDecoder implements AudioDecoder {
    @Override
    public boolean canDecode(Audio audio, AudioCodec target) {
        return false;
    }

    @Override
    public Audio decode(Audio audio, AudioCodec target) {
        return null;
    }
}
