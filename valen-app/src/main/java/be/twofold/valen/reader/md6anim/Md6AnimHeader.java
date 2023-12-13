package be.twofold.valen.reader.md6anim;

import be.twofold.valen.core.util.*;
import be.twofold.valen.geometry.*;

public record Md6AnimHeader(
    String skelName,
    Bounds translatedBounds,
    Bounds normalizedBounds,
    int size
) {
    public static Md6AnimHeader read(BetterBuffer buffer) {
        String skelName = buffer.getString();
        Bounds translatedBounds = Bounds.read(buffer);
        Bounds normalizedBounds = Bounds.read(buffer);
        int size = buffer.getInt();

        return new Md6AnimHeader(
            skelName,
            translatedBounds,
            normalizedBounds,
            size
        );
    }
}
