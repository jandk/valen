package be.twofold.valen.core.audio;

import java.io.*;
import java.util.*;

/**
 * Losslessly repackages audio from one codec into another, the output is still compressed.
 */
public interface AudioConverter {

    AudioCodec source();

    AudioCodec target();

    default boolean canConvert(Audio audio) {
        return audio.codec() == source();
    }

    Audio convert(Audio audio) throws IOException;

    static List<AudioConverter> all() {
        return ServiceLoader.load(AudioConverter.class).stream()
            .map(ServiceLoader.Provider::get)
            .toList();
    }

    static Optional<Audio> convert(Audio audio, AudioCodec target) throws IOException {
        if (audio.codec() == target) {
            return Optional.of(audio);
        }
        for (var converter : all()) {
            if (converter.target() == target && converter.canConvert(audio)) {
                return Optional.of(converter.convert(audio));
            }
        }
        return Optional.empty();
    }

}
