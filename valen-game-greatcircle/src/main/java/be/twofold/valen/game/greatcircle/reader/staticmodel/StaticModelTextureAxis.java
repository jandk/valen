package be.twofold.valen.game.greatcircle.reader.staticmodel;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record StaticModelTextureAxis(
    Quaternion axis,
    Vector3 origin,
    Vector2 scale
) {
    public static StaticModelTextureAxis read(BinarySource source, int version) throws IOException {
        var axis = version < 82
            ? Quaternion.fromMatrix(Matrix3.read(source))
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
