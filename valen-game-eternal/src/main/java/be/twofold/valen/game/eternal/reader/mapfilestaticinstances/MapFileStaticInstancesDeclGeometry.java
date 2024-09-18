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
    public static MapFileStaticInstancesDeclGeometry read(DataSource source) throws IOException {
        var translation = source.readVector3();
        var rotation = source.readMatrix3();
        var extraIndex = source.readInt();
        var unknown52 = source.readInt();

        return new MapFileStaticInstancesDeclGeometry(
            translation,
            rotation,
            extraIndex,
            unknown52
        );
    }
}
