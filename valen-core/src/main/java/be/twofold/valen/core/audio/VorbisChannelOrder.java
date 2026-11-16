package be.twofold.valen.core.audio;

import java.util.*;

public final class VorbisChannelOrder {
    // Standard vorbis order from Vorbis I spec, section 4.3.9
    private static final byte[][] ORDER = {
        null,
        {0},
        {0, 1},
        {0, 2, 1},
        {0, 1, 2, 3},
        {0, 2, 1, 3, 4},
        {0, 2, 1, 5, 3, 4},
        {0, 2, 1, 6, 5, 3, 4},
        {0, 2, 1, 7, 5, 6, 3, 4},
    };

    private VorbisChannelOrder() {
    }

    public static List<AudioChannel> fromWave(List<AudioChannel> channels) {
        int count = channels.size();
        if (count >= ORDER.length) {
            return channels;
        }

        var toWave = ORDER[count];
        var result = new AudioChannel[count];
        for (int w = 0; w < count; w++) {
            result[toWave[w]] = channels.get(w);
        }
        return List.of(result);
    }
}
