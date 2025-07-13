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
    public static Md6AnimHeader read(BinaryReader reader) throws IOException {
        var skelName = reader.readPString();
        reader.expectInt(0); // numAttachments
        var translatedBounds = Bounds.read(reader);
        var normalizedBounds = Bounds.read(reader);
        var size = reader.readInt();

        return new Md6AnimHeader(
            skelName,
            translatedBounds,
            normalizedBounds,
            size
        );
    }
}
