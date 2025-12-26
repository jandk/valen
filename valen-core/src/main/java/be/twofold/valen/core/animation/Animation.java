package be.twofold.valen.core.animation;

import be.twofold.valen.core.geometry.*;
import be.twofold.valen.core.util.*;

import java.util.*;

public record Animation(
    Skeleton skeleton,
    int frameRate,
    List<Track<?>> tracks
) {
    public Animation {
        Check.nonNull(skeleton, "skeleton");
        tracks = List.copyOf(tracks);
    }
}
