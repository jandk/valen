package be.twofold.valen.core.audio;

import java.io.*;
import java.util.*;

/**
 * Decodes audio to {@link AudioCodec#PCM_S16_LE}.
 */
public interface AudioDecoder {

    AudioCodec source();

    default boolean canDecode(Audio audio) {
        return audio.codec() == source();
    }

    Audio decode(Audio audio) throws IOException;

    static List<AudioDecoder> all() {
        return ServiceLoader.load(AudioDecoder.class).stream()
            .map(ServiceLoader.Provider::get)
            .toList();
    }

    static Optional<Audio> toPcm(Audio audio) throws IOException {
        if (audio.codec() == AudioCodec.PCM_S16_LE) {
            return Optional.of(audio);
        }

        var decoders = all();
        var direct = tryDecode(decoders, audio);
        if (direct.isPresent()) {
            return direct;
        }

        for (var converter : AudioConverter.all()) {
            if (converter.canConvert(audio) && decoders.stream().anyMatch(d -> d.source() == converter.target())) {
                var decoded = tryDecode(decoders, converter.convert(audio));
                if (decoded.isPresent()) {
                    return decoded;
                }
            }
        }
        return Optional.empty();
    }

    private static Optional<Audio> tryDecode(List<AudioDecoder> decoders, Audio audio) throws IOException {
        for (var decoder : decoders) {
            if (decoder.canDecode(audio)) {
                return Optional.of(decoder.decode(audio));
            }
        }
        return Optional.empty();
    }

}
