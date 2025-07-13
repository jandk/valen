package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record MapFileStaticInstancesDeclGeometry(
    Vector3 translation,
    Matrix3 rotation,
    int extraIndex,
    int unknown52
) {
    public static MapFileStaticInstancesDeclGeometry read(BinaryReader reader) throws IOException {
        var translation = Vector3.read(reader);
        var rotation = Matrix3.read(reader);
        var extraIndex = reader.readInt();
        var unknown52 = reader.readInt();

        return new MapFileStaticInstancesDeclGeometry(
            translation,
            rotation,
            extraIndex,
            unknown52
        );
    }
}
