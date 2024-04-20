package be.twofold.valen.reader.mapfilestaticinstances;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record MapFileStaticInstancesPlayerStart(
    String entityName,
    Vector3 spawnPosition,
    boolean initial
) {
    public static MapFileStaticInstancesPlayerStart read(BetterBuffer buffer) {
        var entityName = buffer.getString();
        var spawnPosition = Vector3.read(buffer);
        var initial = buffer.getByteAsBool();
        return new MapFileStaticInstancesPlayerStart(
            entityName,
            spawnPosition,
            initial
        );
    }
}
