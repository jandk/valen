package be.twofold.valen.core.texture.convert;

import be.twofold.valen.core.texture.*;

import java.util.*;
import java.util.stream.*;

public record Channels(
    Surface red,
    Surface green,
    Surface blue,
    Surface alpha
) {
    public Channels {
        if (red == null && green == null && blue == null && alpha == null) {
            throw new IllegalArgumentException("Need at least one surface");
        }
        if (!matches(red, green) || !matches(green, blue) || !matches(blue, alpha)) {
            throw new IllegalArgumentException("Not all surfaces match");
        }
    }

    private static boolean matches(Surface first, Surface second) {
        if (first == null || second == null) {
            return true; // If it doesn't exist, it matches
        }
        return first.width() == second.width()
            && first.height() == second.height()
            && first.format() == second.format();
    }

    public int width() {
        return getFirstSurface().width();
    }

    public int height() {
        return getFirstSurface().height();
    }

    public TextureFormat format() {
        return getFirstSurface().format();
    }

    private Surface getFirstSurface() {
        return Stream.of(red, green, blue, alpha)
            .filter(Objects::nonNull)
            .findFirst().orElseThrow();
    }
}
