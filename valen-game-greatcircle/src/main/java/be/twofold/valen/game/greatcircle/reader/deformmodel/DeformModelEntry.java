package be.twofold.valen.game.greatcircle.reader.deformmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record DeformModelEntry(
    String name,
    Vector3 first,
    Vector4 second,
    float f
) {
    public static DeformModelEntry read(DataSource source) throws IOException {
        var name = source.readPString();
        var first = Vector3.read(source);
        var second = Vector4.read(source);
        var f = source.readFloat();
        return new DeformModelEntry(name, first, second, f);
    }
}
