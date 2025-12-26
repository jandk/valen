package be.twofold.valen.game.darkages.reader.anim;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

/**
 * @param translatedBounds animation bounds with translated origin
 * @param normalizedBounds animation bounds without origin translation or rotation
 */
public record Md6AnimHeader(
    String skelName,
    Bounds translatedBounds,
    Bounds normalizedBounds,
    int size
) {
    public static Md6AnimHeader read(BinarySource source) throws IOException {
        var skelName = source.readString(StringFormat.INT_LENGTH);
        source.expectInt(0); // numAttachments
        var translatedBounds = Bounds.read(source);
        var normalizedBounds = Bounds.read(source);
        var size = source.readInt();

        return new Md6AnimHeader(
            skelName,
            translatedBounds,
            normalizedBounds,
            size
        );
    }
}
