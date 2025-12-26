package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.math.*;
import wtf.reversed.toolbox.io.*;

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
    public static MapFileStaticInstancesModelGeometry read(BinarySource source) throws IOException {
        var declLayerIndex = source.readShort();
        var modelIndex = source.readShort();
        var modelIndexVPaint = source.readShort();
        source.expectShort((short) 0);
        var translation = Vector3.read(source);
        var rotation = Matrix3.read(source);
        var scale = Vector3.read(source);
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
