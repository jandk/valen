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
    public static MapFileStaticInstancesModelGeometry read(BinaryReader reader) throws IOException {
        var declLayerIndex = reader.readShort();
        var modelIndex = reader.readShort();
        var modelIndexVPaint = reader.readShort();
        reader.expectShort((short) 0);
        var translation = Vector3.read(reader);
        var rotation = Matrix3.read(reader);
        var scale = Vector3.read(reader);
        var flagsMaybe = reader.readInt();
        var extraIndex = reader.readInt();
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
