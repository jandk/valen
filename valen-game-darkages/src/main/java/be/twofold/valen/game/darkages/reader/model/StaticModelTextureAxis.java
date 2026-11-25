package be.twofold.valen.game.darkages.reader.model;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record StaticModelTextureAxis(
    Matrix3 axis,
    Vector3 origin,
    Vector2 scale
) {
    public static StaticModelTextureAxis read(BinaryReader reader) throws IOException {
        var axis = Matrix3.read(reader);
        var origin = Vector3.read(reader);
        var scale = Vector2.read(reader);

        return new StaticModelTextureAxis(
            axis,
            origin,
            scale
        );
    }
}
