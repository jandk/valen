package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record MapFileStaticInstancesPlayerStart(
    String entityName,
    Vector3 spawnPosition,
    boolean initial
) {
    public static MapFileStaticInstancesPlayerStart read(BinaryReader reader) throws IOException {
        var entityName = reader.readPString();
        var spawnPosition = Vector3.read(reader);
        var initial = reader.readBoolByte();
        return new MapFileStaticInstancesPlayerStart(
            entityName,
            spawnPosition,
            initial
        );
    }
}
