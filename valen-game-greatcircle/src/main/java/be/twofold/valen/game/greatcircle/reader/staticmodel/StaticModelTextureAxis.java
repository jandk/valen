package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelTextureAxis(
    Quaternion axis,
    Vector3 origin,
    Vector2 scale
) {
    public static StaticModelTextureAxis read(BinaryReader reader, int version) throws IOException {
        var axis = version < 82
            ? Matrix3.read(reader).toRotation()
            : Quaternion.read(reader);

        var origin = Vector3.read(reader);
        var scale = Vector2.read(reader);

        return new StaticModelTextureAxis(
            axis,
            origin,
            scale
        );
    }
}
