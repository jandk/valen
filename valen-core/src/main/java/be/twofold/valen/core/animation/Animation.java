package be.twofold.valen.core.animation;

import java.util.*;

public record Animation(
    int frameRate,
    List<Track<?>> tracks
) {
    public Animation {
        tracks = List.copyOf(tracks);
    }
}
