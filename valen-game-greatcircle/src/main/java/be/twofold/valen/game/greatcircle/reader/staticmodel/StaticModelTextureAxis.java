package be.twofold.valen.game.greatcircle.reader.staticmodel;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record StaticModelTextureAxis(
    Quaternion axis,
    Vector3 origin,
    Vector2 scale
) {
    public static StaticModelTextureAxis read(BinarySource source, int version) throws IOException {
        var axis = version < 82
            ? Matrix3.read(source).toRotation()
            : Quaternion.read(source);

        var origin = Vector3.read(source);
        var scale = Vector2.read(source);

        return new StaticModelTextureAxis(
            axis,
            origin,
            scale
        );
    }
}
