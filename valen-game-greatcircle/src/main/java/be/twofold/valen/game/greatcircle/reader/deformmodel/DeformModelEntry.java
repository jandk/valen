package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.io.*;

import java.io.*;

public record DeformModelEntry(
    String name,
    Vector3 first,
    Vector4 second,
    float f
) {
    public static DeformModelEntry read(BinarySource source) throws IOException {
        var name = source.readString(StringFormat.INT_LENGTH);
        var first = Vector3.read(source);
        var second = Vector4.read(source);
        var f = source.readFloat();
        return new DeformModelEntry(name, first, second, f);
    }
}
