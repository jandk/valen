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
    public static DeformModelEntry read(BinaryReader reader) throws IOException {
        var name = reader.readPString();
        var first = Vector3.read(reader);
        var second = Vector4.read(reader);
        var f = reader.readFloat();
        return new DeformModelEntry(name, first, second, f);
    }
}
