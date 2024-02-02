package be.twofold.valen.reader.staticinstances;

import be.twofold.valen.core.math.*;
import be.twofold.valen.core.util.*;

public record StaticInstancePlayerStart(
    String entityName,
    Vector3 spawnPosition,
    boolean initial
) {
    public static StaticInstancePlayerStart read(BetterBuffer buffer) {
        var entityName = buffer.getString();
        var spawnPosition = Vector3.read(buffer);
        var initial = buffer.getByteAsBool();
        return new StaticInstancePlayerStart(
            entityName,
            spawnPosition,
            initial
        );
    }
}
