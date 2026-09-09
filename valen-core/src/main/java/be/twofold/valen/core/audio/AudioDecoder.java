package be.twofold.valen.core.audio;

import java.util.*;

public interface AudioDecoder {

    boolean canDecode(Audio audio, AudioCodec target);

    Audio decode(Audio audio, AudioCodec target);

    static Optional<AudioDecoder> forTarget(Audio audio, AudioCodec target) {
        if (audio.codec() == target) {
            return Optional.empty();
        }
        return ServiceLoader.load(AudioDecoder.class).stream()
            .map(ServiceLoader.Provider::get)
            .filter(d -> d.canDecode(audio, target))
            .findFirst();
    }

    static Optional<Audio> convert(Audio audio, AudioCodec target) {
        if (audio.codec() == target) {
            return Optional.of(audio);
        }
        return forTarget(audio, target)
            .map(d -> d.decode(audio, target));
    }

}
