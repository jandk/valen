package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

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
    public static MapFileStaticInstancesModelGeometry read(DataSource source) throws IOException {
        var declLayerIndex = source.readShort();
        var modelIndex = source.readShort();
        var modelIndexVPaint = source.readShort();
        source.expectShort((short) 0);
        var translation = source.readVector3();
        var rotation = source.readMatrix3();
        var scale = source.readVector3();
        var flagsMaybe = source.readInt();
        var extraIndex = source.readInt();
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
