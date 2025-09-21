package be.twofold.valen.core.texture;

import be.twofold.valen.core.util.*;

import java.util.*;
import java.util.stream.*;

public record Channels(
    Surface red,
    Surface green,
    Surface blue,
    Surface alpha
) {
    public Channels {
        Check.argument(red != null || green != null || blue != null || alpha != null, "Need at least one surface");
        Check.argument(matches(red, green) && matches(green, blue) && matches(blue, alpha), "Not all surfaces match");
    }

    private static boolean matches(Surface first, Surface second) {
        if (first == null || second == null) {
            return true; // If it doesn't exist, it matches
        }
        return first.width() == second.width()
            && first.height() == second.height();
    }

    public int width() {
        return getFirstSurface().width();
    }

    public int height() {
        return getFirstSurface().height();
    }

    private Surface getFirstSurface() {
        return Stream.of(red, green, blue, alpha)
            .filter(Objects::nonNull)
            .findFirst().orElseThrow();
    }
}
