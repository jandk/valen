package be.twofold.valen.game.eternal.reader.mapfilestaticinstances;

import be.twofold.valen.core.io.*;
import be.twofold.valen.core.math.*;

import java.io.*;

public record MapFileStaticInstancesPlayerStart(
    String entityName,
    Vector3 spawnPosition,
    boolean initial
) {
    public static MapFileStaticInstancesPlayerStart read(DataSource source) throws IOException {
        var entityName = source.readPString();
        var spawnPosition = Vector3.read(source);
        var initial = source.readBoolByte();
        return new MapFileStaticInstancesPlayerStart(
            entityName,
            spawnPosition,
            initial
        );
    }
}
