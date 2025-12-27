package be.twofold.valen.core.animation;

import be.twofold.valen.core.geometry.*;
import wtf.reversed.toolbox.util.*;

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
