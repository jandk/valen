package be.twofold.valen.reader.md6anim;

import be.twofold.valen.*;
import be.twofold.valen.geometry.*;

public record Md6AnimHeader(
    String skelName,
    Bounds translatedBounds,
    Bounds normalizedBounds,
    int size
) {
    public static Md6AnimHeader read(BetterBuffer buffer) {
        String skelName = buffer.getString();
        Bounds translatedBounds = new Bounds(buffer.getVector3(), buffer.getVector3());
        Bounds normalizedBounds = new Bounds(buffer.getVector3(), buffer.getVector3());
        int size = buffer.getInt();

        return new Md6AnimHeader(
            skelName,
            translatedBounds,
            normalizedBounds,
            size
        );
    }
}
