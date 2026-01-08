package be.twofold.valen.game.darkages.reader.model;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record StaticModelTextureAxis(
    Matrix3 axis,
    Vector3 origin,
    Vector2 scale
) {
    public static StaticModelTextureAxis read(BinarySource source) throws IOException {
        var axis = Matrix3.read(source);
        var origin = Vector3.read(source);
        var scale = Vector2.read(source);

        return new StaticModelTextureAxis(
            axis,
            origin,
            scale
        );
    }
}
