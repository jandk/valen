package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import wtf.reversed.toolbox.io.*;
import wtf.reversed.toolbox.math.*;

import java.io.*;

public record MapFileStaticInstancesPlayerStart(
    String entityName,
    Vector3 spawnPosition,
    boolean initial
) {
    public static MapFileStaticInstancesPlayerStart read(BinarySource source) throws IOException {
        var entityName = source.readString(StringFormat.INT_LENGTH);
        var spawnPosition = Vector3.read(source);
        var initial = source.readBool(BoolFormat.BYTE);
        return new MapFileStaticInstancesPlayerStart(
            entityName,
            spawnPosition,
            initial
        );
    }
}
