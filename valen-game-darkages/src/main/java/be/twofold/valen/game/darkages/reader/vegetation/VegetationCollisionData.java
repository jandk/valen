package be.twofold.valen.game.darkages.reader.vegetation;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record VegetationCollisionData(
    Vector3 to,
    Vector3 from,
    float radius
) {
    public static VegetationCollisionData read(BinarySource source) throws IOException {
        var to = Vector3.read(source);
        var from = Vector3.read(source);
        var radius = source.readFloat();

        return new VegetationCollisionData(
            to,
            from,
            radius
        );
    }
}
