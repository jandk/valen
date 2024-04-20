package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record MapFileStaticInstancesDeclGeometry(
    Vector3 translation,
    Matrix3 rotation,
    int extraIndex,
    int unknown52
) {
    public static MapFileStaticInstancesDeclGeometry read(BetterBuffer buffer) {
        var translation = Vector3.read(buffer);
        var rotation = Matrix3.read(buffer);
        var extraIndex = buffer.getInt();
        var unknown52 = buffer.getInt();

        return new MapFileStaticInstancesDeclGeometry(
            translation,
            rotation,
            extraIndex,
            unknown52
        );
    }
}
