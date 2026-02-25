package be.twofold.valen.game.eternal.reader.md6anim;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record Md6AnimHeader(
    String skelName,
    Bounds translatedBounds,
    Bounds normalizedBounds,
    int size
) {
    public static Md6AnimHeader read(BinarySource source) throws IOException {
        String skelName = source.readString(StringFormat.INT_LENGTH);
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
