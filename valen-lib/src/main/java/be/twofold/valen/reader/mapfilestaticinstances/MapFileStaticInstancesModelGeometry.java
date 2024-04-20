package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record MapFileStaticInstancesModelGeometry(
    short declLayerIndex,
    short modelIndex,
    short modelIndexVPaint,
    Vector3 translation,
    Matrix3 rotation,
    Vector3 scale,
    int flagsMaybe,
    int extraIndex
) {
    public static MapFileStaticInstancesModelGeometry read(BetterBuffer buffer) {
        var declLayerIndex = buffer.getShort();
        var modelIndex = buffer.getShort();
        var modelIndexVPaint = buffer.getShort();
        buffer.expectShort(0);
        var translation = Vector3.read(buffer);
        var rotation = Matrix3.read(buffer);
        var scale = Vector3.read(buffer);
        var flagsMaybe = buffer.getInt();
        var extraIndex = buffer.getInt();
        return new MapFileStaticInstancesModelGeometry(
            declLayerIndex,
            modelIndex,
            modelIndexVPaint,
            translation,
            rotation,
            scale,
            flagsMaybe,
            extraIndex
        );
    }
}
