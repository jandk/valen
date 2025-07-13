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
    public static Md6AnimHeader read(BinaryReader reader) throws IOException {
        String skelName = reader.readPString();
        Bounds translatedBounds = Bounds.read(reader);
        Bounds normalizedBounds = Bounds.read(reader);
        int size = reader.readInt();

        return new Md6AnimHeader(
            skelName,
            translatedBounds,
            normalizedBounds,
            size
        );
    }
}
