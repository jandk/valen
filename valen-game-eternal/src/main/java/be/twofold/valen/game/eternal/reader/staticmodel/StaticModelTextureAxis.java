package be.twofold.valen.game.eternal.reader.staticmodel;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.io.*;

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
