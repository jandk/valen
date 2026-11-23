package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record MapFileStaticInstancesPlayerStart(
    String entityName,
    Vector3 spawnPosition,
    boolean initial
) {
    public static MapFileStaticInstancesPlayerStart read(BinaryReader reader) throws IOException {
        var entityName = reader.readString(StringFormat.INT_LENGTH);
        var spawnPosition = Vector3.read(reader);
        var initial = reader.readBool(BoolFormat.BYTE);
        return new MapFileStaticInstancesPlayerStart(
            entityName,
            spawnPosition,
            initial
        );
    }
}
