package be.twofold.valen.game.eternal.reader.md6anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record Md6AnimHeader(
    String skelName,
    Bounds translatedBounds,
    Bounds normalizedBounds,
    int size
) {
    public static Md6AnimHeader read(DataSource source) throws IOException {
        String skelName = source.readPString();
        Bounds translatedBounds = Bounds.read(source);
        Bounds normalizedBounds = Bounds.read(source);
        int size = source.readInt();

        return new Md6AnimHeader(
            skelName,
            translatedBounds,
            normalizedBounds,
            size
        );
    }
}
